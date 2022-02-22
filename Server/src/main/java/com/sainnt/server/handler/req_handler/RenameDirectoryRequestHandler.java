package com.sainnt.server.handler.req_handler;

import com.sainnt.server.dto.request.RenameDirectoryRequest;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.service.NavigationService;
import com.sainnt.server.util.InteractionCodes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RenameDirectoryRequestHandler extends SimpleChannelInboundHandler<RenameDirectoryRequest> {
    private final NavigationService service;

    public RenameDirectoryRequestHandler(NavigationService service) {
        this.service = service;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RenameDirectoryRequest request) {
        service.renameDirectory(request.getId(),request.getUser(),request.getNewName());
        CommonReadWriteOperations.sendIntCodeResponse(ctx, InteractionCodes.CODE_OP_RENAMED_DIRECTORY);
    }
}
