package com.sainnt.net.requests;

import com.sainnt.net.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;


public class ListFilesRequest extends Request {


    private final long dirId;

    public ListFilesRequest(long dirId) {
        this.dirId = dirId;
    }

    @Override
    public ChannelFuture perform(Channel channel) {
        ByteBuf request = channel.alloc().buffer(12);
        request.writeInt(23);
        request.writeLong(dirId);
        return channel.writeAndFlush(request);
    }

    public long getDirId() {
        return dirId;
    }

}
