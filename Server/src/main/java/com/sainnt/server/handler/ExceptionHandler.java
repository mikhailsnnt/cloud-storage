package com.sainnt.server.handler;

import com.sainnt.server.exception.ClientAvailableException;
import com.sainnt.server.exception.InternalServerError;
import com.sainnt.server.util.InteractionCodes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        if (cause instanceof ClientAvailableException ex) {
            CommonReadWriteOperations.sendStringWithHeader(ctx,
                    InteractionCodes.getExceptionCode(ex.getClass()),
                    ex.getMessage());
        } else
            CommonReadWriteOperations.sendIntCodeResponse(ctx, InteractionCodes.getExceptionCode(InternalServerError.class));
        log.info("Exception handler exception", cause);
    }
}
