package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.Request;
import com.sainnt.server.dto.request.UploadFileRequest;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.handler.RequestBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;


public class UploadFileRequestBuilder implements RequestBuilder {
    private enum  state{
        readFileSize,
        readPathSize,
        readPath,
        readCheckSumSize,
        readCheckSum,
        completed
    }
    private state currentState;
    private UploadFileRequest request;

    private static final int BASIC_SIZE = 8;
    private final ByteBuf buf;
    private int pathSize = -1;
    private String path;
    private long fileSize = -1;
    private int checkSumSize;

    public UploadFileRequestBuilder() {
        buf = PooledByteBufAllocator.DEFAULT.buffer(BASIC_SIZE);
        currentState = state.readPathSize ;
    }

    @Override
    public boolean addBytesFromByteBuf(ByteBuf in) {
        switch (currentState) {
            case readPathSize:
                pathSize = CommonReadWriteOperations.readIntHeader(in, buf);
                if (pathSize == -1)
                    break;
                CommonReadWriteOperations.ensureCapacity(buf,pathSize);
                currentState = state.readPath;

            case readPath:
                path = CommonReadWriteOperations.readString(in, pathSize, buf);
                if (path == null)
                    break;
                currentState = state.readFileSize;
            case readFileSize:
                fileSize = CommonReadWriteOperations.readLongHeader(in, buf);
                if (fileSize == -1)
                    break;
                currentState = state.readCheckSumSize;
            case readCheckSumSize:
                checkSumSize = CommonReadWriteOperations.readIntHeader(in,buf);
                if(checkSumSize ==-1)
                    break;
                CommonReadWriteOperations.ensureCapacity(buf, checkSumSize);
                currentState = state.readCheckSum;

            case readCheckSum:
                if(buf.readableBytes() < checkSumSize)
                    in.readBytes(buf,Math.min(in.readableBytes(), checkSumSize - buf.readableBytes()));
                if (buf.readableBytes() >= checkSumSize){
                    request = new UploadFileRequest();
                    request.setPath(path);
                    request.setFileSize(fileSize);
                    byte[] c = new byte[checkSumSize];
                    buf.readBytes(c,0,checkSumSize);
                    request.setCheckSum(c);
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
