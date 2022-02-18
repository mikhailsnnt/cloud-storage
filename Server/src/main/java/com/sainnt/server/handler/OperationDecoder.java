package com.sainnt.server.handler;

import com.sainnt.server.dto.request.Request;
import com.sainnt.server.entity.User;
import com.sainnt.server.service.AuthenticationService;
import com.sainnt.server.service.operations.ByteUploadOperation;
import com.sainnt.server.util.InteractionCodes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class OperationDecoder extends ByteToMessageDecoder {
    private final AuthenticationService authService;
    private final User user;


    public OperationDecoder(User user, AuthenticationService authService) {
        this.user = user;
        this.authService = authService;
    }

    private ByteUploadOperation transferringOperation;
    private RequestBuilder requestBuilder;

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        if (requestBuilder != null) {
            requestBuilder.releaseResources();
        }
        authService.userDisconnected(user.getId());
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        transferInput(channelHandlerContext, byteBuf, list);
        if (byteBuf.readableBytes() < InteractionCodes.HEADER_SIZE)
            return;
        int operationCode = byteBuf.readInt();
        if (operationCode == InteractionCodes.CODE_EXIT) {
            try {
                channelHandlerContext.close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Optional<Class<? extends RequestBuilder>> requestBuilderClass = InteractionCodes.getRequestBuilderClass(operationCode);
        if (requestBuilderClass.isPresent()) {
            try {
                requestBuilder = requestBuilderClass.get().getDeclaredConstructor().newInstance();
            } catch (Exception exception) {
                log.error("Request builder creating error", exception);
            }
        } else
            CommonReadWriteOperations.sendIntCodeResponse(channelHandlerContext, InteractionCodes.CODE_INVALID_REQUEST);
        if (byteBuf.readableBytes() > 0)
            transferInput(channelHandlerContext, byteBuf, list);
    }

    public void transferInput(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {
        if (transferringOperation != null) {
            if (transferringOperation.transferBytesFromByteBuf(byteBuf)) {
                CommonReadWriteOperations.sendIntCodeResponse(ctx, InteractionCodes.CODE_UPLOADED_SUCCESSFULLY);
                transferringOperation = null;
            }
        }
        if (requestBuilder != null) {
            if (requestBuilder.addBytesFromByteBuf(byteBuf)) {
                Request request = requestBuilder.getResultRequest();
                request.setUser(user);
                requestBuilder = null;
                list.add(request);
            }
        }
    }

    public void setTransferringOperation(ByteUploadOperation transferringOperation) {
        this.transferringOperation = transferringOperation;
    }
}
