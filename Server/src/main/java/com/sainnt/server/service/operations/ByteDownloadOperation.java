package com.sainnt.server.service.operations;

import io.netty.buffer.ByteBuf;

public interface ByteDownloadOperation {
    long getSize();
    ByteBuf getBytePortion();
    byte[] getCheckSum();
    boolean bytesLeft();
    void cancel();
}
