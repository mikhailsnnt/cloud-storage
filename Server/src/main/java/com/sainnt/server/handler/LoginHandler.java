package com.sainnt.server.handler;

import com.sainnt.server.dto.RegistrationResult;
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
        regOrLogin,readLoginSize,readLogin,readEmailSize,readEmail,readPasswordSize, readPassword
    }
    private final PipeLineBuilder pipeBuilder;
    private ByteBuf headerBuffer;
    private ByteBuf contentBuffer;
    private final int headerSize;
    private int loginSize = -1;
    private int passwordSize = -1;
    private int emailSize = -1;
    private authState currentState;

    private boolean isRegistering;
    private String username;
    private String email;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        headerBuffer = ctx.alloc().buffer(headerSize);
        currentState = authState.regOrLogin;
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
        ByteBuf in = (ByteBuf) msg;
        if(in.readableBytes()<1)
            return;
        switch (currentState)
        {
            case regOrLogin:
                byte b = in.readByte();
                if (b==0){
                    isRegistering = false;
                    currentState = authState.readLoginSize;
                }
                else if(b == 1)
                {
                    isRegistering = true;
                    currentState = authState.readLoginSize;
                }
                else
                    return;
            case readLoginSize:
                loginSize = readHeader(in,ctx);
                if (loginSize == -1)
                    return;
                currentState = authState.readLogin;
            case readLogin:
                username = readString(in,loginSize);
                if(username ==null)
                    return;
                if(isRegistering)
                    currentState = authState.readEmailSize;
                else
                    currentState = authState.readPasswordSize;


            case readEmailSize:
                emailSize = readHeader(in,ctx);
                if(emailSize==-1)
                    return;
                currentState = authState.readEmail;

            case readEmail:
                email = readString(in,emailSize);
                if(email==null)
                    return;
                currentState = authState.readPasswordSize;

            case readPasswordSize:
                passwordSize = readHeader(in,ctx);
                if(passwordSize ==-1)
                    return;
                currentState = authState.readPassword;

            case readPassword:
                contentBuffer.
                        writeBytes(in,
                                Math.min(in.readableBytes(),
                                        passwordSize - contentBuffer.readableBytes()));
                if(contentBuffer.readableBytes()<passwordSize)
                    return;
                byte[] password = new byte[passwordSize];
                contentBuffer.readBytes(password);
                if(isRegistering)
                {
                    RegistrationResult regResult = authService.registerUser(username, email, password);
                    if(regResult==RegistrationResult.success)
                        sendCode(ctx, 50);
                    else if(regResult == RegistrationResult.email_invalid)
                        sendCode(ctx, 51);
                    else if(regResult == RegistrationResult.password_invalid)
                        sendCode(ctx, 52);
                    else if(regResult == RegistrationResult.username_occupied)
                        sendCode(ctx, 53);
                    else if(regResult == RegistrationResult.email_exists)
                        sendCode(ctx, 54);
                    else if(regResult == RegistrationResult.registration_failed)
                        sendCode(ctx,55);
                }
                else
                {
                    try{
                        User user = authService.authenticate(username, password);
                        if(user==null) {
                            //Auth failed
                            sendCode(ctx, 101);
                        }
                        else {
                            sendCode(ctx, 100);
                            pipeBuilder.buildUserPipeLine(ctx.pipeline(),user);
                            ctx.pipeline().remove(this);
                        }
                    }catch (UserAlreadyLoggedInException exception){
                        //User is logged in
                        sendCode(ctx, 102);
                    }
                }
                ctx.flush();
                username =null;
                email = null;
                contentBuffer.clear();
                currentState = authState.regOrLogin;
        }
        in.release();


    }

    private void sendCode(ChannelHandlerContext ctx, int code) {
        ctx.write(ctx.alloc().buffer(4).writeInt(code));
    }


    private int readHeader(ByteBuf in, ChannelHandlerContext ctx){
        headerBuffer
                .writeBytes(in,
                        Math.min(in.readableBytes(),
                                headerSize - headerBuffer.readableBytes()));
        if(headerBuffer.readableBytes() < headerSize)
            return -1;
        int header = headerBuffer.readInt();
        if (contentBuffer==null)
            contentBuffer = ctx.alloc().buffer(header);
        if(contentBuffer.capacity()<header) {
            contentBuffer.capacity( header - contentBuffer.capacity());
        }
        headerBuffer.clear();
        return header;
    }

    private String readString(ByteBuf in, int strSize){
        contentBuffer.
                writeBytes(in,
                        Math.min(in.readableBytes(),
                                strSize - contentBuffer.readableBytes()));
        if(contentBuffer.readableBytes() < strSize)
            return null;
        String str = contentBuffer.readCharSequence(strSize, StandardCharsets.UTF_8).toString();
        contentBuffer.clear();
        return str;
    }

    public LoginHandler(int HEADER_SIZE, PipeLineBuilder builder, AuthenticationService authService) {
        this.headerSize = HEADER_SIZE;
        this.pipeBuilder = builder;
        this.authService = authService;
    }

}
