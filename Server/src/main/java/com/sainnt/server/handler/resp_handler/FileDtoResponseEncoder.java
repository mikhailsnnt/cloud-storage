package com.sainnt.server.handler.resp_handler;

import com.sainnt.server.dto.FileDto;
import com.sainnt.server.handler.CommonReadWriteOperations;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class FileDtoResponseEncoder extends MessageToByteEncoder<FileDto> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, FileDto fileDto, ByteBuf byteBuf) throws Exception {
        //Protocol:
        // isDir-0/1
        //completed-0/1
        //name.size
        //name
        //File? size
        ByteBuf buf;
        if (fileDto.isDirectory()) {
            buf = channelHandlerContext.alloc().buffer(6+fileDto.getName().length());
            buf.writeBoolean(true);
            buf.writeBoolean(fileDto.isCompleted());
            buf.writeInt(fileDto.getName().length());
            buf.writeBytes(fileDto.getName().getBytes(StandardCharsets.UTF_8));
        } else {
            buf  = channelHandlerContext.alloc().buffer(14+fileDto.getName().length());
            buf.writeBoolean(false);
            buf.writeBoolean(fileDto.isCompleted());
            buf.writeInt(fileDto.getName().length());
            buf.writeBytes(fileDto.getName().getBytes(StandardCharsets.UTF_8));
            buf.writeLong(fileDto.getSize());
        }
        channelHandlerContext.writeAndFlush(buf);
    }
}