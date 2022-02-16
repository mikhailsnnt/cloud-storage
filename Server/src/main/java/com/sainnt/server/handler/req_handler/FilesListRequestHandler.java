package com.sainnt.server.handler.req_handler;

import com.sainnt.server.dto.FileDto;
import com.sainnt.server.dto.request.FilesListRequest;
import com.sainnt.server.service.FileOperationsService;
import com.sainnt.server.util.InteractionCodes;
import io.netty.buffer.ByteBuf;
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
        ByteBuf buf = ctx.alloc().buffer(12);
        buf.writeInt(InteractionCodes.CODE_OP_LIST_FILES);
        buf.writeLong(request.getId());
        ctx.writeAndFlush(buf).sync();
        ctx.writeAndFlush(files);
    }
}
