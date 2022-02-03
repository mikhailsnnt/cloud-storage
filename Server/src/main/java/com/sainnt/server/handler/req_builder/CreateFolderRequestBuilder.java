package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.CreateDirectoryRequest;
import com.sainnt.server.dto.request.Request;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.handler.RequestBuilder;
import com.sainnt.server.util.InteractionCodes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.charset.StandardCharsets;


public class CreateFolderRequestBuilder implements RequestBuilder {
    private enum  state{
        readPathSize,
        readPath,
        completed
    }
    private CreateDirectoryRequest request;
    private final ByteBuf buf;
    private state currentState;
    private int pathSize = -1;

    public CreateFolderRequestBuilder() {
        buf = PooledByteBufAllocator.DEFAULT.buffer(InteractionCodes.HEADER_SIZE);
        currentState = state.readPathSize;
    }

    @Override
    public boolean addBytesFromByteBuf(ByteBuf in) {
        switch (currentState)
        {
            case readPathSize:
                pathSize = CommonReadWriteOperations.readIntHeader(in,buf);
                if(pathSize!=-1)
                {
                    CommonReadWriteOperations.ensureCapacity(buf,pathSize);
                    currentState = state.readPath;
                }
            case readPath:
                String s = CommonReadWriteOperations.readString(in,pathSize,buf);
                if(s != null)
                {
                    request = new CreateDirectoryRequest();
                    request.setPath(buf.readCharSequence(pathSize, StandardCharsets.UTF_8).toString());
                    buf.release();
                    currentState = state.completed;
                    return true;
                }
        }
        return false;
    }

    @Override
    public Request getResultRequest() {
        return request;
    }

    @Override
    public void releaseResources() {
        buf.release();
    }
}
