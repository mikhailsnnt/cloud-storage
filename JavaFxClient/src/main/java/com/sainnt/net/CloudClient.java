package com.sainnt.net;

import com.sainnt.dto.SignInResult;
import com.sainnt.dto.SignUpResult;
import com.sainnt.files.FileRepresentation;
import com.sainnt.net.handler.LoginHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
public class CloudClient {
    private final HashMap<String, ObservableList<FileRepresentation>> listFilesRequests = new HashMap<>();
    private boolean connected = false;
    private static CloudClient client;
    private final HashMap<String, File> fileUploadRequests = new HashMap<>();
    private EventLoopGroup workerGroup;

    public synchronized static CloudClient getClient() {
        if (client == null) {
            client = new CloudClient();
            client.initConnection();
        }
        return client;
    }

    private Channel channel;
    private Task<Channel> connectTask;

    public void initConnection() {
        if (connected) {
            log.info("initConnection() declined, already connected");
            return;
        }
        workerGroup = new NioEventLoopGroup();
        connectTask = new Task<>() {
            @Override
            protected Channel call() throws Exception {
                Bootstrap b = new Bootstrap();                    // (1)
                b.group(workerGroup);                             // (2)
                b.channel(NioSocketChannel.class)
                        .remoteAddress(new InetSocketAddress("localhost", 9096));                // (3)
                b.option(ChannelOption.SO_KEEPALIVE, true);
                b.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
//                        handler = new OperationHandler();
//                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter());

                    }
                });
                ChannelFuture f = b.connect();
                Channel chn = f.channel();
                f.sync();
                return chn;
            }

            @Override
            protected void succeeded() {
                log.info("Connected successfully");
                channel = getValue();
                connected = true;
            }

            @Override
            protected void failed() {
                workerGroup.shutdownGracefully();
                Throwable e = getException();
                log.error("Connection error", e);
                connected = false;
            }
        };
        Thread thread = new Thread(connectTask);
        thread.setDaemon(false);
        thread.start();
    }

    public void closeConnection() {
        if (!connected) {
            log.info("Connection close declined,not connected");
            return;
        }
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

    public void requestChildrenFiles(String path, ObservableList<FileRepresentation> children) {
        if (!connected) {
            log.info("Files list request denied, not connected to server");
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ByteBuf buf = channel.alloc().buffer(8 + path.length());
                buf.writeInt(23);
                buf.writeInt(path.length());
                buf.writeBytes(path.getBytes(StandardCharsets.UTF_8));
                channel.writeAndFlush(buf).sync();
                return null;
            }
        };
        listFilesRequests.put(path, children);
        Thread thread = new Thread(task);
        thread.start();
    }

    public void createRemoteDirectory(String path) {
        if (!connected) {
            log.info("Create remote directory denied, not connected to server");
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                ByteBuf buf = channel.alloc().buffer(8 + path.length());
                buf.writeInt(20);
                buf.writeInt(path.length());
                buf.writeBytes(path.getBytes(StandardCharsets.UTF_8));
                channel.writeAndFlush(buf);
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    public void handleFilesRequest(String path, Collection<FileRepresentation> files) {
        ObservableList<FileRepresentation> filesDestination = listFilesRequests.get(path);
        filesDestination.clear();
        filesDestination.addAll(files);
    }

    public void uploadFile(String path, File file) {
        if (!connected) {
            log.info("File upload declined, not connected to server");
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ByteBuf buf = channel.alloc().buffer(16 + path.length());
                buf.writeInt(21);
                buf.writeInt(path.length());
                buf.writeBytes(path.getBytes(StandardCharsets.UTF_8));
                buf.writeLong(file.length());
                channel.writeAndFlush(buf).sync();
                fileUploadRequests.put(path, file);
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    public void handleFileUploadResponse(String remotePath) {
        File file = fileUploadRequests.get(remotePath);
        FileRegion fileRegion = new DefaultFileRegion(file, 0, file.length());
        channel.writeAndFlush(fileRegion);
    }

    public void renameFileRequest(String path, String name) {
        // Not implemented on server side yet
    }

    public void deleteFileRequest(String path) {
        if(!connected){
            log.info("File delete declined, not connected to server");
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                ByteBuf buf = channel.alloc().buffer(8 + path.length());
                buf.writeInt(22);
                buf.writeInt(path.length());
                buf.writeBytes(path.getBytes(StandardCharsets.UTF_8));
                channel.writeAndFlush(buf);
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.start();

    }

    public void deleteDirectoryRequest(String path) {
        // Not implemented on server side yet
    }
}
