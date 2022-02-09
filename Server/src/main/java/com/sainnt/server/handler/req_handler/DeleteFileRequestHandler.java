package com.sainnt.server.handler.req_handler;

import com.sainnt.server.dto.request.DeleteFileRequest;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.service.NavigationService;
import com.sainnt.server.util.InteractionCodes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DeleteFileRequestHandler extends SimpleChannelInboundHandler<DeleteFileRequest> {
    private final NavigationService navigationService;

    public DeleteFileRequestHandler(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DeleteFileRequest deleteFileRequest) {
        navigationService.deleteFile(deleteFileRequest.getPath(), deleteFileRequest.getUser());
        CommonReadWriteOperations.sendIntCodeResponse(channelHandlerContext, InteractionCodes.CODE_OP_DELETED_FILE);
    }
}
