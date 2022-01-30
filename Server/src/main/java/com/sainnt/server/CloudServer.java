package com.sainnt.server;

import com.sainnt.server.dao.UserRepository;
import com.sainnt.server.dao.impl.UserRepositoryImpl;
import com.sainnt.server.handler.LoginHandler;
import com.sainnt.server.pipebuilder.PipeLineBuilder;
import com.sainnt.server.pipebuilder.impl.PipeLineBuilderImpl;
import com.sainnt.server.security.PasswordEncryptionProvider;
import com.sainnt.server.security.impl.PasswordEncryptionProviderImpl;
import com.sainnt.server.service.AuthenticationService;
import com.sainnt.server.service.impl.AuthenticationServiceImpl;
import com.sainnt.server.util.HibernateUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class CloudServer {
    private final int port;
    private UserRepository userRepository;
    private AuthenticationService authenticationService;
    private PasswordEncryptionProvider passwordEncryptionProvider;
    private PipeLineBuilder pipeLineBuilder;
    public CloudServer(int port) {
        this.port = port;
    }

    public void run () throws Exception{
        HibernateUtil.getSessionFactory().openSession();
        injectDependencies();
        EventLoopGroup acceptGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(acceptGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new LoginHandler(4,pipeLineBuilder,authenticationService));
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

    private void injectDependencies(){
        userRepository = new UserRepositoryImpl();
        passwordEncryptionProvider = new PasswordEncryptionProviderImpl();
        pipeLineBuilder = new PipeLineBuilderImpl();
        authenticationService = new AuthenticationServiceImpl(userRepository,passwordEncryptionProvider);


    }

    public static void main(String[] args) throws Exception {
        int port = 9096;
        if(args.length>0)
            port = Integer.parseInt(args[0]);
        (new CloudServer(port)).run();
    }

}
