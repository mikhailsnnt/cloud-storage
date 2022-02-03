package com.sainnt.server.service.operations;

import io.netty.buffer.ByteBuf;

public interface ByteTransferringOperation {
    boolean transferBytesFromByteBuf(ByteBuf buf);
    void interrupt();
}
