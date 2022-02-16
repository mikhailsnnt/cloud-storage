package com.sainnt.server.handler.resp_handler;

import com.sainnt.server.dto.FileDto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class FileDtoResponseEncoder extends MessageToByteEncoder<FileDto> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, FileDto fileDto, ByteBuf byteBuf) {
        ByteBuf buf;
        if (fileDto.isDirectory()) {
            buf = channelHandlerContext.alloc().buffer(14 + fileDto.getName().length());
            buf.writeLong(fileDto.getId());
            buf.writeBoolean(true);
            buf.writeBoolean(fileDto.isCompleted());
            buf.writeInt(fileDto.getName().length());
            buf.writeBytes(fileDto.getName().getBytes(StandardCharsets.UTF_8));
        } else {
            buf = channelHandlerContext.alloc().buffer(22 + fileDto.getName().length());
            buf.writeLong(fileDto.getId());
            buf.writeBoolean(false);
            buf.writeBoolean(fileDto.isCompleted());
            buf.writeInt(fileDto.getName().length());
            buf.writeBytes(fileDto.getName().getBytes(StandardCharsets.UTF_8));
            buf.writeLong(fileDto.getSize());
        }
        channelHandlerContext.writeAndFlush(buf);
    }
}
