package com.sainnt.server.handler;

import com.sainnt.server.entity.User;
import com.sainnt.server.exception.UserAlreadyLoggedInException;
import com.sainnt.server.pipebuilder.PipeLineBuilder;
import com.sainnt.server.service.AuthenticationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class LoginHandler extends ChannelInboundHandlerAdapter {
    private final AuthenticationService authService;
    private enum authState {
        readLoginSize,readLogin,readPasswordSize, readPassword
    }
    private final PipeLineBuilder pipeBuilder;
    private ByteBuf headerBuffer;
    private ByteBuf contentBuffer;
    private final int sizeHeader;
    private int loginSize = -1;
    private int passwordSize = -1;
    private authState currentState;

    private String login = "";


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        headerBuffer = ctx.alloc().buffer(sizeHeader);
        currentState = authState.readLoginSize;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        headerBuffer.release();
        headerBuffer =null;
        if(contentBuffer != null) {
            contentBuffer.release();
            contentBuffer = null;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //For performance avoided many function calls
        ByteBuf in = (ByteBuf) msg;
        switch (currentState)
        {
            case readLoginSize:
                headerBuffer
                        .writeBytes(in,
                                Math.min(in.readableBytes(),
                                        sizeHeader - headerBuffer.readableBytes()));
                if(headerBuffer.readableBytes() < sizeHeader)
                    return;
                loginSize = headerBuffer.readInt();
                currentState = authState.readLogin;
                if (contentBuffer==null)
                    contentBuffer = ctx.alloc().buffer(loginSize);
                if(contentBuffer.capacity()<loginSize) {
                    contentBuffer.capacity( loginSize - contentBuffer.capacity());
                }
                headerBuffer.clear();
            case readLogin:
                contentBuffer.
                        writeBytes(in,
                                Math.min(in.readableBytes(),
                                        loginSize - contentBuffer.readableBytes()));
                if(contentBuffer.readableBytes() < loginSize)
                    return;
                login = contentBuffer.readCharSequence(loginSize, StandardCharsets.UTF_8).toString();
                currentState = authState.readPasswordSize;
                contentBuffer.clear();
            case readPasswordSize:
                headerBuffer
                        .writeBytes(in,
                                Math.min(in.readableBytes(),
                                        sizeHeader - headerBuffer.readableBytes()));
                if(headerBuffer.readableBytes()<sizeHeader)
                    return;
                passwordSize = headerBuffer.readInt();
                currentState = authState.readPassword;
                if(contentBuffer.capacity()<passwordSize) {
                    contentBuffer.capacity( passwordSize - contentBuffer.capacity());
                }
                headerBuffer.clear();
            case readPassword:
                contentBuffer.
                        writeBytes(in,
                                Math.min(in.readableBytes(),
                                        passwordSize - contentBuffer.readableBytes()));
                if(contentBuffer.readableBytes()<passwordSize)
                    return;
                byte[] password = new byte[passwordSize];
                contentBuffer.readBytes(password);
                try{
                    User user = authService.authenticate(login, password);
                    if(user==null) {
                        //Auth failed
                        ctx.write(ctx.alloc().buffer(4).writeInt(101));
                    }
                    else {
                        ctx.write(ctx.alloc().buffer(4).writeInt(100));
                        pipeBuilder.buildUserPipeLine(ctx.pipeline(),user);
                        ctx.pipeline().remove(this);
                    }
                }catch (UserAlreadyLoggedInException exception){
                    //User is logged in
                    ctx.write(ctx.alloc().buffer(4).writeInt(102));
                }
                ctx.flush();
                contentBuffer.clear();
        }
        in.release();


    }




    public LoginHandler(int HEADER_SIZE, PipeLineBuilder builder, AuthenticationService authService) {
        this.sizeHeader = HEADER_SIZE;
        this.pipeBuilder = builder;
        this.authService = authService;
    }

}
