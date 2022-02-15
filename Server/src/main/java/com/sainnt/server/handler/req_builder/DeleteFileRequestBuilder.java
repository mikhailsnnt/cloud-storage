package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.DeleteFileRequest;
import com.sainnt.server.dto.request.Request;

public class DeleteFileRequestBuilder extends IdRequestBuilder {
    @Override
    protected Request formRequest(long id) {
        DeleteFileRequest req = new DeleteFileRequest();
        req.setId(id);
        return req;
    }
}
