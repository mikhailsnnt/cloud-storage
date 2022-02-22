package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.DeleteDirectoryRequest;
import com.sainnt.server.dto.request.Request;

public class DeleteDirectoryRequestBuilder extends IdRequestBuilder{
    @Override
    protected Request formRequest(long id) {
        return new DeleteDirectoryRequest(id);
    }
}
