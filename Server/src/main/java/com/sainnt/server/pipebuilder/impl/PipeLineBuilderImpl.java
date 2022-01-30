package com.sainnt.server.pipebuilder.impl;

import com.sainnt.server.entity.User;
import com.sainnt.server.pipebuilder.PipeLineBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

public class PipeLineBuilderImpl implements PipeLineBuilder {
    @Override
    public void buildUserPipeLine(ChannelPipeline pipeline, User user) {
        //not implemented. Only demonstration
        pipeline.addLast( new  ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                System.out.println( ((ByteBuf)msg).readByte());
            }
        });
    }
}
