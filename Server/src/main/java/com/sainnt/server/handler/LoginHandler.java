package com.sainnt.server.handler;

import com.sainnt.server.dto.LoginResult;
import com.sainnt.server.dto.RegistrationResult;
import com.sainnt.server.pipebuilder.PipeLineBuilder;
import com.sainnt.server.service.AuthenticationService;
import com.sainnt.server.util.InteractionCodes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LoginHandler extends ChannelInboundHandlerAdapter {
    private final AuthenticationService authService;

    private enum authState {
        regOrLogin, readLoginSize, readLogin, readEmailSize, readEmail, readPasswordSize, readPassword
    }

    private final PipeLineBuilder pipeBuilder;
    private ByteBuf headerBuffer;
    private ByteBuf contentBuffer;
    private int loginSize = -1;
    private int passwordSize = -1;
    private int emailSize = -1;
    private authState currentState;

    private boolean isRegistering;
    private String username;
    private String email;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        headerBuffer = ctx.alloc().buffer(InteractionCodes.HEADER_SIZE);
        currentState = authState.regOrLogin;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        headerBuffer.release();
        headerBuffer = null;
        if (contentBuffer != null) {
            contentBuffer.release();
            contentBuffer = null;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        if (in.readableBytes() < 1)
            return;
        switch (currentState) {
            case regOrLogin:
                int op_code = CommonReadWriteOperations.readIntHeader(in, headerBuffer);
                if (op_code == -1)
                    return;
                if (op_code == InteractionCodes.CODE_LOGIN) {
                    isRegistering = false;
                    currentState = authState.readLoginSize;
                } else if (op_code == InteractionCodes.CODE_REGISTER) {
                    isRegistering = true;
                    currentState = authState.readLoginSize;
                } else {
                    CommonReadWriteOperations.sendIntCodeResponse(ctx, InteractionCodes.CODE_INVALID_REQUEST);
                }
            case readLoginSize:
                loginSize = CommonReadWriteOperations.readIntHeader(in, headerBuffer);
                if (loginSize == -1)
                    return;
                if (contentBuffer == null)
                    contentBuffer = ctx.alloc().buffer(loginSize);
                CommonReadWriteOperations.ensureCapacity(contentBuffer, loginSize);
                currentState = authState.readLogin;
            case readLogin:
                username = CommonReadWriteOperations.readString(in, loginSize, contentBuffer);
                if (username == null)
                    return;
                if (isRegistering)
                    currentState = authState.readEmailSize;
                else
                    currentState = authState.readPasswordSize;


            case readEmailSize:
                if (isRegistering) {
                    emailSize = CommonReadWriteOperations.readIntHeader(in, headerBuffer);
                    if (emailSize == -1)
                        return;
                    CommonReadWriteOperations.ensureCapacity(contentBuffer, emailSize);
                    currentState = authState.readEmail;
                }

            case readEmail:
                if (isRegistering) {
                    email = CommonReadWriteOperations.readString(in, emailSize, contentBuffer);
                    if (email == null)
                        return;
                    currentState = authState.readPasswordSize;
                }

            case readPasswordSize:
                passwordSize = CommonReadWriteOperations.readIntHeader(in, headerBuffer);
                if (passwordSize == -1)
                    return;
                CommonReadWriteOperations.ensureCapacity(contentBuffer, passwordSize);
                currentState = authState.readPassword;

            case readPassword:
                contentBuffer.
                        writeBytes(in,
                                Math.min(in.readableBytes(),
                                        passwordSize - contentBuffer.readableBytes()));
                if (contentBuffer.readableBytes() < passwordSize)
                    return;
                byte[] password = new byte[passwordSize];
                contentBuffer.readBytes(password);
                if (isRegistering) {
                    RegistrationResult regResult = authService.registerUser(username, email, password);
                    CommonReadWriteOperations.sendIntCodeResponse(ctx, InteractionCodes.getRegistrationResultCode(regResult));
                } else {
                    LoginResult res = authService.authenticate(username, password);
                    CommonReadWriteOperations.sendIntCodeResponse(ctx, InteractionCodes.getLoginResultCode(res.getResult()));
                    if (res.getResult() == LoginResult.Result.success) {
                        pipeBuilder.buildUserPipeLine(ctx.pipeline(), res.getUser());
                        ctx.pipeline().remove(this);
                    }
                }
                ctx.flush();
                username = null;
                email = null;
                contentBuffer.clear();
                currentState = authState.regOrLogin;
        }
        in.release();


    }


    public LoginHandler(PipeLineBuilder builder, AuthenticationService authService) {
        this.pipeBuilder = builder;
        this.authService = authService;
    }

}
