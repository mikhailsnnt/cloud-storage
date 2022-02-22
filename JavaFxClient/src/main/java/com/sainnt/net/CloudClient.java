package com.sainnt.net;

import com.sainnt.dto.SignInResult;
import com.sainnt.dto.SignUpResult;
import com.sainnt.files.FileRepresentation;
import com.sainnt.files.RemoteFileRepresentation;
import com.sainnt.net.handler.LoginHandler;
import com.sainnt.net.requests.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
public class CloudClient {
    public CloudClient() {
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("localhost", 9096));
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {

            }
        });
    }

    private static CloudClient client;
    private boolean connected = false;
    private final EventLoopGroup workerGroup;
    private final  Bootstrap bootstrap;
    private Request currentRequest;
    private boolean performingRequest;
    private final BlockingQueue<Request> requestQueue = new ArrayBlockingQueue<>(15);
    private final Map<Long, RemoteFileRepresentation> idToRemoteFile = new HashMap<>();
    private long downloadFileSize;
    private long bytesRead;
    private BufferedOutputStream fileOutputStream;

    private Runnable onRequestStarted;
    private Runnable onRequestCompleted;

    public void setOnRequestStarted(Runnable onRequestStarted) {
        this.onRequestStarted = onRequestStarted;
    }

    public void setOnRequestCompleted(Runnable onRequestCompleted) {
        this.onRequestCompleted = onRequestCompleted;
    }

    public synchronized static CloudClient getClient() {
        return client;
    }

    public synchronized static void connect(Runnable callback, Consumer<String> exceptionAlertProvider){
        if (client == null) {
            client = new CloudClient();
        }
        if(!client.connected)
            client.initConnection(callback,exceptionAlertProvider);
    }

    private Channel channel;
    private Task<Channel> connectTask;

    public void initConnection(Runnable callback, Consumer<String> exceptionAlertProvider) {
        if (connected) {
            log.info("initConnection() declined, already connected");
            return;
        }

        connectTask = new Task<>() {
            @Override
            protected Channel call() throws Exception {

                ChannelFuture f = bootstrap.connect();
                Channel chn = f.channel();
                f.sync();
                return chn;
            }

            @Override
            protected void succeeded() {
                log.info("Connected successfully");
                channel = getValue();
                connected = true;
                callback.run();
            }

            @Override
            protected void failed() {
                Throwable e = getException();
                exceptionAlertProvider.accept(e.getMessage());
                log.error("Connection error", e);
                connected = false;

            }
        };
        Thread thread = new Thread(connectTask);
        thread.setDaemon(false);
        thread.start();
    }

    public void closeConnection() {
        workerGroup.shutdownGracefully();
    }

    public void authenticate(String login, String password) {
        if (!connected) {
            log.info("Login declined client not connected");
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws InterruptedException {
                ByteBuf buf = channel.alloc().buffer(12 + login.length() + password.length());
                buf.writeInt(5);
                buf.writeInt(login.length());
                buf.writeBytes(login.getBytes(StandardCharsets.UTF_8));
                buf.writeInt(password.length());
                buf.writeBytes(password.getBytes(StandardCharsets.UTF_8));
                channel.writeAndFlush(buf).sync();
                return null;
            }

            @Override
            protected void failed() {
                log.error("Error during authentication:", getException());
                connected = false;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void register(String login, String email, String password) {
        if (!connected) {
            log.info("Registration declined client not connected");
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws InterruptedException {
                ByteBuf buf = channel.alloc().buffer(16 + login.length() + email.length() + password.length());
                buf.writeInt(6);
                buf.writeInt(login.length());
                buf.writeBytes(login.getBytes(StandardCharsets.UTF_8));
                buf.writeInt(email.length());
                buf.writeBytes(email.getBytes(StandardCharsets.UTF_8));
                buf.writeInt(password.length());
                buf.writeBytes(password.getBytes(StandardCharsets.UTF_8));
                channel.writeAndFlush(buf).sync();
                return null;
            }

            @Override
            protected void failed() {
                log.error("Error during registration:", getException());
                connected = false;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void initLoginHandler(Consumer<SignInResult> signInResultConsumer, Consumer<SignUpResult> signUpResultConsumer) {
        if (!connected) {
            try {
                channel = connectTask.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        channel.pipeline().addLast(new LoginHandler(signInResultConsumer, signUpResultConsumer));
    }

    public void requestChildrenFiles(long id) {
        requestQueue.add(new ListFilesRequest(id));
        pollRequest();
    }

    private synchronized void pollRequest() {
        if (!performingRequest && !requestQueue.isEmpty()) {
            currentRequest = requestQueue.poll();
            performingRequest = true;
            if(onRequestStarted != null)
                onRequestStarted.run();
            currentRequest.perform(channel);
        }
    }

    public void createRemoteDirectory(long parentId, String name) {
        requestQueue.add(new CreateDirectoryRequest(name, parentId));
        pollRequest();
    }

    public void handleFilesRequest(long id, Collection<RemoteFileRepresentation> files) {
        if (currentRequest instanceof ListFilesRequest) {
            assert id == ((ListFilesRequest) currentRequest).getDirId();
            RemoteFileRepresentation parent = idToRemoteFile.get(id);
            ObservableList<FileRepresentation> filesDestination = parent.getChildren();
            filesDestination.clear();
            files.forEach(file -> {
                file.setParent(parent);
                filesDestination.add(file);
                idToRemoteFile.put(file.getId(), file);
            });
            completeRequest();
        } else
            System.out.println("Current request is not hfr");
    }

    private void completeRequest() {
        performingRequest = false;
        currentRequest = null;
        if(onRequestCompleted!=null)
            onRequestCompleted.run();
        pollRequest();
    }

    public void uploadFile(long dirId, File file) {
        requestQueue.add(new UploadFileRequest(dirId, file));
        pollRequest();
    }

    public void handleFileUploadResponse(long dirId, String name, long fileId) {
        RemoteFileRepresentation dir = idToRemoteFile.get(dirId);
        RemoteFileRepresentation newFile = new RemoteFileRepresentation(fileId, dir, name, false);
        addRemoteFileRepresentation(newFile);
        dir.getChildren().add(newFile);
        File file = ((UploadFileRequest) currentRequest).getFile();
        FileRegion fileRegion = new DefaultFileRegion(file, 0, file.length());
        channel.writeAndFlush(fileRegion);
    }

    public void renameFileRequest(long id, String name, Runnable onComplete) {
        requestQueue.add(new RenameFileRequest(id,name, idToRemoteFile.get(id).getName(),onComplete));
        pollRequest();
    }
    public void renameDirectoryRequest(long id, String name, Runnable onComplete) {
        requestQueue.add(new RenameDirectoryRequest(id,name, idToRemoteFile.get(id).getName(),onComplete));
        pollRequest();
    }

    public void deleteFileRequest(long id) {
        requestQueue.add(new DeleteFileRequest(id));
        pollRequest();
    }

    public void deleteDirectoryRequest(long id) {
        requestQueue.add(new DeleteDirectoryRequest(id));
        pollRequest();
    }

    public void addRemoteFileRepresentation(RemoteFileRepresentation item) {
        idToRemoteFile.put(item.getId(), item);
    }

    public boolean isPerformingRequest() {
        return performingRequest;
    }

    public void fileUploadCompleted() {
        completeRequest();
    }

    public void deleteFileCompleted() {
        if (currentRequest instanceof DeleteFileRequest request) {
            long deletedId = request.getId();
            RemoteFileRepresentation file = idToRemoteFile.get(deletedId);
            file.getParent().getChildren().remove(file);
            completeRequest();
        }
    }

    public void downloadFile(long id, File file) {
        requestQueue.add(new DownloadFileRequests(id, file));
        pollRequest();
    }


    public boolean handleFileDownloadPortion(ByteBuf byteBuf) {
        if (currentRequest instanceof DownloadFileRequests requests) {
            if (fileOutputStream == null) {
                try {
                    fileOutputStream = new BufferedOutputStream(new FileOutputStream(requests.getDestination()));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                int bytesToRead = (int) Math.min(downloadFileSize - bytesRead, byteBuf.readableBytes());
                byteBuf.readBytes(fileOutputStream, bytesToRead);
                bytesRead += bytesToRead;
                if (bytesRead == downloadFileSize) {
                    fileOutputStream.close();
                    fileOutputStream = null;
                    completeRequest();
                    return true;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public void setDownloadFileSize(long size) {
        downloadFileSize = size;
        bytesRead = 0;
    }

    public void fileRenameCompleted() {
        if(currentRequest instanceof RenameFileRequest request){
            idToRemoteFile.get(request.getId()).setName(request.getNewName());
            request.getOnComplete().run();
        }
        completeRequest();
    }
    public void directoryRenameCompleted() {
        if(currentRequest instanceof RenameDirectoryRequest request){
            idToRemoteFile.get(request.getId()).setName(request.getNewName());
            request.getOnComplete().run();
        }
        completeRequest();
    }

    public void exceptionCaught() {
        completeRequest();
    }

    public void directoryDeleteCompleted() {
        if(currentRequest instanceof DeleteDirectoryRequest request){
            RemoteFileRepresentation dir = idToRemoteFile.get(request.getId());
            dir.getParent().getChildren().remove(dir);
            idToRemoteFile.remove(dir.getId());
        }
        completeRequest();
    }

    public void directoryCreateCompleted(long dirId) {
        if(currentRequest instanceof CreateDirectoryRequest request){
            RemoteFileRepresentation parentDir = idToRemoteFile.get(request.getParentId());
            RemoteFileRepresentation newDir = new RemoteFileRepresentation(dirId,parentDir,request.getName(),true);
            addRemoteFileRepresentation(newDir);
            parentDir.getChildren().add(newDir);
        }
        completeRequest();
    }
}
