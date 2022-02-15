package com.sainnt.server.handler.req_handler;

import com.sainnt.server.dto.request.CreateDirectoryRequest;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.service.NavigationService;
import com.sainnt.server.util.InteractionCodes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CreateFolderRequestHandler extends SimpleChannelInboundHandler<CreateDirectoryRequest> {
    private final NavigationService service;

    public CreateFolderRequestHandler(NavigationService service) {
        this.service = service;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CreateDirectoryRequest request) {
        service.createDirectory(request.getParentId(), request.getName(), request.getUser());
        CommonReadWriteOperations.sendIntCodeResponse(channelHandlerContext, InteractionCodes.CODE_OP_CREATED_DIR);
    }
}
