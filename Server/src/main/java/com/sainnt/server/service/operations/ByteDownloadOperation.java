package com.sainnt.server.service.operations;

import io.netty.channel.FileRegion;

public interface ByteDownloadOperation {
    long getSize();

    FileRegion getFileRegion();
}
