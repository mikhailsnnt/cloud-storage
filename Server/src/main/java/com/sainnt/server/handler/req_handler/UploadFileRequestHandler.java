package com.sainnt.server.handler.req_handler;

import com.sainnt.server.dto.request.UploadFileRequest;
import com.sainnt.server.handler.OperationDecoder;
import com.sainnt.server.service.FileOperationsService;
import com.sainnt.server.service.operations.ByteUploadOperation;
import com.sainnt.server.util.InteractionCodes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

public class UploadFileRequestHandler extends SimpleChannelInboundHandler<UploadFileRequest> {
    private final FileOperationsService service;
    private final OperationDecoder decoder;

    public UploadFileRequestHandler(FileOperationsService service, OperationDecoder decoder) {
        this.service = service;
        this.decoder = decoder;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UploadFileRequest uploadFileRequest) throws Exception {
        ByteUploadOperation fileUploadOperation = service.uploadFile(uploadFileRequest);
        ByteBuf resp = ctx.alloc().buffer(16 + uploadFileRequest.getName().length());
        resp.writeInt(InteractionCodes.CODE_START_UPLOAD);
        resp.writeLong(uploadFileRequest.getParentId());
        resp.writeInt(uploadFileRequest.getName().length());
        resp.writeBytes(uploadFileRequest.getName().getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(resp).sync();
        decoder.setTransferringOperation(fileUploadOperation);
    }
}
