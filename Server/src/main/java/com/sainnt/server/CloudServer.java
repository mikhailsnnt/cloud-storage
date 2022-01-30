package com.sainnt.server;

import com.sainnt.server.handler.LoginHandler;
import com.sainnt.server.pipebuilder.PipeLineBuilder;
import com.sainnt.server.pipebuilder.impl.PipeLineBuilderImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class CloudServer {
    private final int port;

    public CloudServer(int port) {
        this.port = port;
    }

    public void run () throws Exception{
        EventLoopGroup acceptGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        PipeLineBuilder builder = new PipeLineBuilderImpl();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(acceptGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new LoginHandler(4,builder));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,120)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
        }
        finally {
            acceptGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 9096;
        if(args.length>0)
            port = Integer.parseInt(args[0]);
        (new CloudServer(port)).run();
    }

}
