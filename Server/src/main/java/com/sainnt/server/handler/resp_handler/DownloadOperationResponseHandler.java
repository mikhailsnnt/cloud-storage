package com.sainnt.server.handler.resp_handler;

import com.sainnt.server.service.operations.ByteDownloadOperation;
import com.sainnt.server.util.InteractionCodes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class DownloadOperationResponseHandler extends MessageToByteEncoder<ByteDownloadOperation> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteDownloadOperation o, ByteBuf byteBuf) throws InterruptedException {
        ByteBuf responseBuf = ctx.alloc().buffer(12);
        responseBuf.writeInt(InteractionCodes.CODE_OP_START_DOWNLOAD);
        responseBuf.writeLong(o.getSize());
        ctx.writeAndFlush(responseBuf).sync();
        ctx.writeAndFlush(o.getFileRegion());
    }
}
