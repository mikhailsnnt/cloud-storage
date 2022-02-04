package com.sainnt.server.handler.req_handler;

import com.sainnt.server.dto.request.DownloadFileRequest;
import com.sainnt.server.service.FileOperationsService;
import com.sainnt.server.service.operations.ByteDownloadOperation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DownloadFileRequestHandler extends SimpleChannelInboundHandler<DownloadFileRequest> {
    private final FileOperationsService service;

    public DownloadFileRequestHandler(FileOperationsService service) {
        this.service = service;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DownloadFileRequest downloadFileRequestHandler) throws Exception {
        ByteDownloadOperation downloadOperation = service.downloadFile(downloadFileRequestHandler);
        channelHandlerContext.writeAndFlush(downloadOperation);
    }
}
