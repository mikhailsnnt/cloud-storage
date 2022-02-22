package com.sainnt.net.requests;

import com.sainnt.net.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class DeleteDirectoryRequest extends Request {
    private final long id;

    public DeleteDirectoryRequest(long id) {
        this.id = id;
    }

    @Override
    public ChannelFuture perform(Channel channel) {
        ByteBuf buf = channel.alloc().buffer(12);
        buf.writeInt(27);
        buf.writeLong(id);
        return channel.writeAndFlush(buf);
    }

    public long getId() {
        return id;
    }
}
