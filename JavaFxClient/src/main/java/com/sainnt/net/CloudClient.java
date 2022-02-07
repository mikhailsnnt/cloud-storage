package com.sainnt.net;

import com.sainnt.dto.SignInResult;
import com.sainnt.dto.SignUpResult;
import com.sainnt.net.handler.LoginHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
public class CloudClient {
    private boolean connected = false;
    private static CloudClient client;

    public synchronized static CloudClient getClient() {
        if(client==null)
        {
            client = new  CloudClient();
            client.initConnection();
        }
        return client;
    }

    private  Channel channel ;
    private Task<Channel> connectTask ;
    public  void initConnection() {
        if(connected)
        {
            log.info("initConnection() declined, already connected");
            return;
        }
        EventLoopGroup workerGroup = new NioEventLoopGroup();
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
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter());

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
        thread.start();
    }

    public void authenticate(String login, String password){
        if(!connected) {
            log.info ("Login declined client not connected");
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
        new Thread(task).start();
    }
    public void register(String login, String email, String password){
        if(!connected)
        {
            log.info ("Registration declined client not connected");
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
        new Thread(task).start();
    }

    public void initLoginHandler(Consumer<SignInResult> signInResultConsumer, Consumer<SignUpResult> signUpResultConsumer ){
        if(!connected) {
            try {
                channel = connectTask.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        channel.pipeline().addLast(new LoginHandler(signInResultConsumer, signUpResultConsumer));
    }
}