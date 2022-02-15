package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.Request;
import com.sainnt.server.dto.request.UploadFileRequest;
import com.sainnt.server.handler.CommonReadWriteOperations;
import com.sainnt.server.handler.RequestBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;


public class UploadFileRequestBuilder implements RequestBuilder {
    private enum state {
        readFileSize,
        readParentId,
        readNameSize,
        readName,
        readCheckSumSize,
        readCheckSum,
        completed
    }

    private state currentState;
    private UploadFileRequest request;

    private static final int BASIC_SIZE = 8;
    private final ByteBuf buf;
    private int nameSize = -1;
    private String name;
    private int checkSumSize;
    private long parentId = -1;

    public UploadFileRequestBuilder() {
        buf = PooledByteBufAllocator.DEFAULT.buffer(BASIC_SIZE);
        currentState = state.readParentId;
    }

    @Override
    public boolean addBytesFromByteBuf(ByteBuf in) {
        switch (currentState) {
            case readParentId:
                parentId = CommonReadWriteOperations.readLongHeader(in, buf);
                if (parentId == -1)
                    break;
                currentState = state.readNameSize;
            case readNameSize:
                nameSize = CommonReadWriteOperations.readIntHeader(in, buf);
                if (nameSize == -1)
                    break;
                CommonReadWriteOperations.ensureCapacity(buf, nameSize);
                currentState = state.readName;

            case readName:
                name = CommonReadWriteOperations.readString(in, nameSize, buf);
                if (name == null)
                    break;
                currentState = state.readFileSize;
            case readFileSize:
                long fileSize = CommonReadWriteOperations.readLongHeader(in, buf);
                if (fileSize == -1)
                    break;
                request = new UploadFileRequest();
                request.setParentId(parentId);
                request.setFileSize(fileSize);
                request.setName(name);
                currentState = state.completed;
                return true;

//            case readCheckSumSize:
//                checkSumSize = CommonReadWriteOperations.readIntHeader(in,buf);
//                if(checkSumSize ==-1)
//                    break;
//                CommonReadWriteOperations.ensureCapacity(buf, checkSumSize);
//                currentState = state.readCheckSum;
//
//            case readCheckSum:
//                if(buf.readableBytes() < checkSumSize)
//                    in.readBytes(buf,Math.min(in.readableBytes(), checkSumSize - buf.readableBytes()));
//                if (buf.readableBytes() >= checkSumSize){
//                    request = new UploadFileRequest();
//                    request.setPath(path);
//                    request.setFileSize(fileSize);
//                    byte[] c = new byte[checkSumSize];
//                    buf.readBytes(c,0,checkSumSize);
//                    request.setCheckSum(c);
//                    currentState = state.completed;
//                    return true;
//                }

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
