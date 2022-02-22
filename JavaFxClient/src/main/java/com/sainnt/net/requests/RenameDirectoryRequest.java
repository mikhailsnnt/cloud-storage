package com.sainnt.net.requests;

import com.sainnt.net.UndoableRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.nio.charset.StandardCharsets;

public class RenameDirectoryRequest extends UndoableRequest {
    private final long id;
    private final String newName;
    private final String oldName;
    private final Runnable onComplete;

    public RenameDirectoryRequest(long id, String newName, String oldName, Runnable onComplete) {
        this.id = id;
        this.newName = newName;
        this.oldName = oldName;
        this.onComplete = onComplete;
    }

    public long getId() {
        return id;
    }

    public String getNewName() {
        return newName;
    }

    public String getOldName() {
        return oldName;
    }

    @Override
    public ChannelFuture perform(Channel channel) {
        ByteBuf buf = channel.alloc().buffer(16 + newName.length());
        buf.writeInt(26);
        buf.writeLong(id);
        buf.writeInt(newName.length());
        buf.writeBytes(newName.getBytes(StandardCharsets.UTF_8));
        return channel.writeAndFlush(buf);
    }

    @Override
    public void undo() {

    }

    public Runnable getOnComplete() {
        return onComplete;
    }
}
