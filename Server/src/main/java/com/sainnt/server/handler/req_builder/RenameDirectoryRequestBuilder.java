package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.RenameDirectoryRequest;
import com.sainnt.server.dto.request.Request;

public class RenameDirectoryRequestBuilder extends IdAndStringRequestBuilder{
    @Override
    protected Request formRequest(long id, String str) {
        return new RenameDirectoryRequest(id,str);
    }
}
