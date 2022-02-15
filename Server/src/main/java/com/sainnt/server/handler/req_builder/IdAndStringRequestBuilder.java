package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.Request;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.handler.RequestBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public abstract class IdAndStringRequestBuilder implements RequestBuilder {
    private enum state {
        readId,
        readStringSize,
        readString,
        completed
    }

    private Request request;
    private final ByteBuf buf;
    private state currentState;
    private long id = -1;
    private int pathSize = -1;

    public IdAndStringRequestBuilder() {
        buf = PooledByteBufAllocator.DEFAULT.buffer(8);
        currentState = state.readId;
    }

    @Override
    public boolean addBytesFromByteBuf(ByteBuf in) {
        switch (currentState) {
            case readId:
                id = CommonReadWriteOperations.readLongHeader(in, buf);
                if (id == -1)
                    break;
                currentState = state.readStringSize;
            case readStringSize:
                pathSize = CommonReadWriteOperations.readIntHeader(in, buf);
                if (pathSize == -1)
                    break;
                CommonReadWriteOperations.ensureCapacity(buf, pathSize);
                currentState = state.readString;

            case readString:
                String s = CommonReadWriteOperations.readString(in, pathSize, buf);
                if (s != null) {
                    request = formRequest(id, s);
                    releaseResources();
                    currentState = state.completed;
                    return true;
                }
        }
        return false;
    }

    protected abstract Request formRequest(long id, String str);


    @Override
    public Request getResultRequest() {
        return request;
    }

    @Override
    public void releaseResources() {
        buf.release();
    }
}
