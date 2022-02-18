package com.sainnt.net.requests;

import com.sainnt.net.UndoableRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class UploadFileRequest extends UndoableRequest {
    private final long dirId;
    private final File file;

    public UploadFileRequest(long dirId, File file) {
        this.dirId = dirId;
        this.file = file;
    }

    public long getDirId() {
        return dirId;
    }

    public File getFile() {
        return file;
    }

    @Override
    public ChannelFuture perform(Channel channel) {
        String fileName = file.getName();
        ByteBuf buf = channel.alloc().buffer(24 + fileName.length());
        buf.writeInt(21);
        buf.writeLong(dirId);
        buf.writeInt(fileName.length());
        buf.writeBytes(fileName.getBytes(StandardCharsets.UTF_8));
        buf.writeLong(file.length());
        return channel.writeAndFlush(buf);
    }

    @Override
    public void undo() {

    }
}
