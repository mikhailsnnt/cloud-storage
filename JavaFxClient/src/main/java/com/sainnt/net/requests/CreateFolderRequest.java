package com.sainnt.net.requests;

import com.sainnt.net.UndoableRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.nio.charset.StandardCharsets;

public class CreateFolderRequest extends UndoableRequest {
    private final String name;
    private final long parentId;

    public CreateFolderRequest(String name, long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    @Override
    public ChannelFuture perform(Channel channel) {
        ByteBuf buf = channel.alloc().buffer(16 + name.length());
        buf.writeInt(20);
        buf.writeLong(parentId);
        buf.writeInt(name.length());
        buf.writeBytes(name.getBytes(StandardCharsets.UTF_8));
        return channel.writeAndFlush(buf);
    }

    @Override
    public void undo() {

    }
}
