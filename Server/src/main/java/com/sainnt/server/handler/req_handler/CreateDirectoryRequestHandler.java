package com.sainnt.server.handler.req_handler;

import com.sainnt.server.dto.request.CreateDirectoryRequest;
import com.sainnt.server.entity.Directory;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.service.NavigationService;
import com.sainnt.server.util.InteractionCodes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CreateDirectoryRequestHandler extends SimpleChannelInboundHandler<CreateDirectoryRequest> {
    private final NavigationService service;

    public CreateDirectoryRequestHandler(NavigationService service) {
        this.service = service;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateDirectoryRequest request) {
        Directory dir = service.createDirectory(request.getParentId(), request.getName(), request.getUser());
        ByteBuf buf = ctx.alloc().buffer(12);
        buf.writeInt(InteractionCodes.CODE_OP_CREATED_DIR);
        buf.writeLong(dir.getId());
        ctx.writeAndFlush(buf);
    }
}
