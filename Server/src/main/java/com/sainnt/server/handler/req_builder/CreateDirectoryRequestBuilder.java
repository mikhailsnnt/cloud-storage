package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.CreateDirectoryRequest;
import com.sainnt.server.dto.request.Request;


public class CreateDirectoryRequestBuilder extends OneStringRequestBuilder {

    @Override
    protected Request formRequest(String str) {
        CreateDirectoryRequest req = new CreateDirectoryRequest();
        req.setPath(str);
        return  req;
    }
}
