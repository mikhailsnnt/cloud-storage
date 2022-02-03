package com.sainnt.server.handler;

import com.sainnt.server.dto.request.Request;
import io.netty.buffer.ByteBuf;

public interface RequestBuilder {
    boolean addBytesFromByteBuf(ByteBuf buf);
    Request getResultRequest();
    void releaseResources();
}
