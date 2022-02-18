package com.sainnt.server.service.operations;

import io.netty.buffer.ByteBuf;

public interface ByteUploadOperation {
    boolean transferBytesFromByteBuf(ByteBuf buf);

    void interrupt();

    long getUploadedId();
}
