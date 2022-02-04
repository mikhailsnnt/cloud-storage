package com.sainnt.server.handler.req_handler;

import com.sainnt.server.dto.request.UploadFileRequest;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.handler.OperationDecoder;
import com.sainnt.server.service.FileOperationsService;
import com.sainnt.server.service.operations.ByteUploadOperation;
import com.sainnt.server.util.InteractionCodes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class UploadFileRequestHandler extends SimpleChannelInboundHandler<UploadFileRequest> {
    private final FileOperationsService service;
    private final OperationDecoder decoder;
    public UploadFileRequestHandler(FileOperationsService service, OperationDecoder decoder) {
        this.service = service;
        this.decoder = decoder;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, UploadFileRequest uploadFileRequest) {
        ByteUploadOperation fileUploadOperation = service.uploadFile(uploadFileRequest);
        decoder.setTransferringOperation(fileUploadOperation);
        CommonReadWriteOperations.sendIntCodeResponse(channelHandlerContext, InteractionCodes.CODE_START_UPLOAD);
    }
}
