package com.sainnt.server.handler;

import com.sainnt.server.util.InteractionCodes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

public class CommonReadWriteOperations {
    private CommonReadWriteOperations(){}
    public static String readString(ByteBuf in, int strSize, ByteBuf buf){
        buf.
                writeBytes(in,
                        Math.min(in.readableBytes(),
                                strSize - buf.readableBytes()));
        if(buf.readableBytes() < strSize)
            return null;
        String str = buf.readCharSequence(strSize, StandardCharsets.UTF_8).toString();
        buf.clear();
        return str;
    }
    public static int readIntHeader(ByteBuf in, ByteBuf buf){
        buf
                .writeBytes(in,
                        Math.min(in.readableBytes(),
                                4 - buf.readableBytes()));
        if(buf.readableBytes() < 4)
            return -1;
        int header = buf.readInt();
        buf.clear();
        return header;
    }
    public static long readLongHeader(ByteBuf in, ByteBuf buf){
        buf
                .writeBytes(in,
                        Math.min(in.readableBytes(),
                                8 - buf.readableBytes()));
        if(buf.readableBytes() < 8)
            return -1;
        long header = buf.readLong();
        buf.clear();
        return header;
    }

    public static void sendIntCodeResponse(ChannelHandlerContext ctx, int code) {
        ctx.write(ctx.alloc().buffer(InteractionCodes.HEADER_SIZE).writeInt(code));
        ctx.flush();
    }

    public static void ensureCapacity(ByteBuf buf, int size){
        if(buf.capacity()<size)
            buf.capacity(size);
    }

    public static void sendStringWithHeader(ChannelHandlerContext ctx, String message) {
        ctx.write(ctx.alloc().buffer(InteractionCodes.HEADER_SIZE).writeInt(message.length()));
        ctx.write(ctx.alloc().buffer(message.length()).writeBytes(message.getBytes(StandardCharsets.UTF_8)));
        ctx.flush();
    }
}
