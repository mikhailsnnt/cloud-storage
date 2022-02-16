package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.CreateDirectoryRequest;
import com.sainnt.server.dto.request.Request;


public class CreateDirectoryRequestBuilder extends IdAndStringRequestBuilder {

    @Override
    protected Request formRequest(long id, String str) {
        return new CreateDirectoryRequest(id,str);
    }
}
