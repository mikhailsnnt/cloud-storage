package com.sainnt.server;

import com.sainnt.server.handler.LoginHandler;
import com.sainnt.server.handler.RootHandler;
import com.sainnt.server.pipebuilder.PipeLineBuilder;
import com.sainnt.server.service.AuthenticationService;
import com.sainnt.server.util.HibernateUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class CloudServer {
    private int port = 9096;

    private final PipeLineBuilder pipeLineBuilder;
    private final AuthenticationService authenticationService;

    public CloudServer(AuthenticationService authenticationService, PipeLineBuilder pipeLineBuilder) {
        this.authenticationService = authenticationService;
        this.pipeLineBuilder = pipeLineBuilder;
    }

    public void run() throws Exception {
        HibernateUtil.getSessionFactory().openSession();
        EventLoopGroup acceptGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(acceptGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new RootHandler());
                            socketChannel.pipeline().addLast(new LoginHandler(pipeLineBuilder, authenticationService));
                        }

                    })
                    .option(ChannelOption.SO_BACKLOG, 120)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
        } finally {
            acceptGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 9096;
        if (args.length > 0)
            port = Integer.parseInt(args[0]);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("com.sainnt.server");
        context.refresh();
        CloudServer server = context.getBean(CloudServer.class);
        server.setPort(port);
        server.run();
    }

}
