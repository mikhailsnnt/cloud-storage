package com.sainnt.server.handler.req_handler;

import com.sainnt.server.dto.request.RenameFileRequest;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.service.NavigationService;
import com.sainnt.server.util.InteractionCodes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RenameFileRequestHandler extends SimpleChannelInboundHandler<RenameFileRequest> {
    private final NavigationService service;

    public RenameFileRequestHandler(NavigationService service) {
        this.service = service;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RenameFileRequest renameFileRequest) {
        service.renameFile(renameFileRequest.getId(), renameFileRequest.getUser(),renameFileRequest.getNewName());
        CommonReadWriteOperations.sendIntCodeResponse(ctx,InteractionCodes.CODE_OP_RENAMED_FILE);
    }
}
