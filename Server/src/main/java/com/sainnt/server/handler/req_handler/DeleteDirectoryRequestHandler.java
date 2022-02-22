package com.sainnt.server.handler.req_handler;

import com.sainnt.server.dto.request.DeleteDirectoryRequest;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.service.NavigationService;
import com.sainnt.server.util.InteractionCodes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DeleteDirectoryRequestHandler extends SimpleChannelInboundHandler<DeleteDirectoryRequest> {
    private final NavigationService service;

    public DeleteDirectoryRequestHandler(NavigationService service) {
        this.service = service;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DeleteDirectoryRequest request) {
        service.deleteDirectory(request.getId(), request.getUser());
        CommonReadWriteOperations.sendIntCodeResponse(ctx,InteractionCodes.CODE_OP_DELETED_DIRECTORY);
    }
}
