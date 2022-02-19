package com.sainnt.net.requests;

import com.sainnt.net.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.io.File;

public class DownloadFileRequests extends Request {
    private final long id;
    private final File destination;

    public DownloadFileRequests(long id, File destination) {
        this.id = id;
        this.destination = destination;
    }

    @Override
    public ChannelFuture perform(Channel channel) {
        ByteBuf buf = channel.alloc().buffer(12);
        buf.writeInt(24);
        buf.writeLong(id);
        return channel.writeAndFlush(buf);
    }

    public long getId() {
        return id;
    }

    public File getDestination() {
        return destination;
    }
}
