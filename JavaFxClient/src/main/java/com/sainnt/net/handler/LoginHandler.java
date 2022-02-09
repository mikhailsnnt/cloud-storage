package com.sainnt.net.handler;

import com.sainnt.dto.SignInResult;
import com.sainnt.dto.SignUpResult;
import com.sainnt.net.OperationHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class LoginHandler extends ChannelInboundHandlerAdapter {
    private final Consumer<SignInResult> signInResultConsumer;
    private final Consumer<SignUpResult> signUpResultConsumer;
    private ByteBuf buf;

    public LoginHandler(Consumer<SignInResult> signInResultConsumer, Consumer<SignUpResult> signUpResultConsumer) {
        this.signInResultConsumer = signInResultConsumer;
        this.signUpResultConsumer = signUpResultConsumer;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer(4);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (buf == null)
            buf = ctx.alloc().buffer(4);
        ByteBuf in = (ByteBuf) msg;
        if (in.readableBytes() <= 0)
            return;
        buf.writeBytes(in, Math.min(in.readableBytes(), 4 - buf.readableBytes()));
        int code = buf.readInt();
        log.debug("Login handler received code {}", code);
        switch (code) {
            case 100:
                removeLoginController(ctx);
                Platform.runLater(() -> signInResultConsumer.accept(SignInResult.success));
                break;
            case 101:
                Platform.runLater(() -> signInResultConsumer.accept(SignInResult.bad_credentials));
                break;
            case 102:
                Platform.runLater(() -> signInResultConsumer.accept(SignInResult.user_already_logged_in));
                break;
            case 50:
                Platform.runLater(() -> signUpResultConsumer.accept(SignUpResult.success));
                break;
            case 51:
                Platform.runLater(() -> signUpResultConsumer.accept(SignUpResult.email_invalid));
                break;
            case 52:
                Platform.runLater(() -> signUpResultConsumer.accept(SignUpResult.password_invalid));
                break;
            case 53:
                Platform.runLater(() -> signUpResultConsumer.accept(SignUpResult.username_occupied));
                break;
            case 54:
                Platform.runLater(() -> signUpResultConsumer.accept(SignUpResult.email_exists));
                break;
            case 55:
                Platform.runLater(() -> signUpResultConsumer.accept(SignUpResult.registration_failed));
                break;

        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        buf.release();
    }

    private void removeLoginController(ChannelHandlerContext ctx) {
        ctx.pipeline().addLast(new OperationHandler(t -> showError(t.getExceptionType(), t.getDetails())));
        ctx.pipeline().remove(this);
    }

    private void showError(String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.show();
        });
    }
}
