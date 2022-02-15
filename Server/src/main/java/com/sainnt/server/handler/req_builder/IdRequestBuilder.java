package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.Request;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.handler.RequestBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public abstract class IdRequestBuilder implements RequestBuilder {

    private Request request;
    private boolean isComplete;
    private final ByteBuf buf;

    public IdRequestBuilder() {
        buf = PooledByteBufAllocator.DEFAULT.buffer(8);
    }

    @Override
    public boolean addBytesFromByteBuf(ByteBuf in) {
        if (isComplete)
            return true;
        long id = CommonReadWriteOperations.readLongHeader(in, buf);
        if (id != -1) {
            request = formRequest(id);
            isComplete = true;
            return true;
        }
        return false;
    }

    protected abstract Request formRequest(long id);


    @Override
    public Request getResultRequest() {
        return request;
    }

    @Override
    public void releaseResources() {
        buf.release();
    }
}
