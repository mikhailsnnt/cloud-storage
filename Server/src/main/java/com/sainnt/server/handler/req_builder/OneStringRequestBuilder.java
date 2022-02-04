package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.Request;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.handler.RequestBuilder;
import com.sainnt.server.util.InteractionCodes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;


public abstract class OneStringRequestBuilder implements RequestBuilder {
    private enum  state{
        readStringSize,
        readString,
        completed
    }
    private Request request;
    private final ByteBuf buf;
    private state currentState;
    private int pathSize = -1;

    public OneStringRequestBuilder() {
        buf = PooledByteBufAllocator.DEFAULT.buffer(InteractionCodes.HEADER_SIZE);
        currentState = state.readStringSize;
    }

    @Override
    public boolean addBytesFromByteBuf(ByteBuf in) {
        switch (currentState)
        {
            case readStringSize:
                pathSize = CommonReadWriteOperations.readIntHeader(in,buf);
                if(pathSize==-1)
                    break;
                CommonReadWriteOperations.ensureCapacity(buf,pathSize);
                currentState = state.readString;

            case readString:
                String s = CommonReadWriteOperations.readString(in,pathSize,buf);
                if(s != null)
                {
                    request = formRequest(s);
                    buf.release();
                    currentState = state.completed;
                    return true;
                }
        }
        return false;
    }

    protected abstract Request  formRequest(String str);


    @Override
    public Request getResultRequest() {
        return request;
    }

    @Override
    public void releaseResources() {
        buf.release();
    }
}
