package com.sainnt.net.requests;

import com.sainnt.net.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class DeleteFileRequest extends Request {
    private final long id;

    public long getId() {
        return id;
    }

    public DeleteFileRequest(long id) {
        this.id = id;
    }

    @Override
    public ChannelFuture perform(Channel channel) {
        ByteBuf buf = channel.alloc().buffer(12);
        buf.writeInt(22);
        buf.writeLong(id);
        return channel.writeAndFlush(buf);
    }
}
