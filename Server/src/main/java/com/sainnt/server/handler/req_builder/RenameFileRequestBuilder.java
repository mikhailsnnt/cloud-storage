package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.RenameFileRequest;
import com.sainnt.server.dto.request.Request;

public class RenameFileRequestBuilder extends IdAndStringRequestBuilder{
    @Override
    protected Request formRequest(long id, String str) {
        return new RenameFileRequest(id,str);
    }
}
