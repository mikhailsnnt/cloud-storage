package com.sainnt.server.handler.req_handler;

import com.sainnt.server.dto.FileDto;
import com.sainnt.server.dto.request.FilesListRequest;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.service.FileOperationsService;
import com.sainnt.server.util.InteractionCodes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

public class FilesListRequestHandler extends SimpleChannelInboundHandler<FilesListRequest> {
    private final FileOperationsService service;

    public FilesListRequestHandler(FileOperationsService service) {
        this.service = service;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FilesListRequest request) throws InterruptedException {
        List<FileDto> files = service.getFiles(request);
        CommonReadWriteOperations.sendStringWithHeader(ctx,
                InteractionCodes.CODE_OP_LIST_FILES,
                request.getPath()).sync();
        ctx.writeAndFlush(files);
    }
}
