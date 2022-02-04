package com.sainnt.server.handler.resp_handler;

import com.sainnt.server.dto.FileDto;
import com.sainnt.server.handler.CommonReadWriteOperations;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

public class FilesListResponseHandler extends MessageToByteEncoder<List<FileDto>> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, List<FileDto> file, ByteBuf byteBuf)  {
        CommonReadWriteOperations.sendIntCodeResponse(channelHandlerContext,file.size());
        file.forEach(channelHandlerContext::write);
    }
}
