package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.DeleteFileRequest;
import com.sainnt.server.dto.request.Request;

public class DeleteFileRequestBuilder  extends OneStringRequestBuilder {
    @Override
    protected Request formRequest(String str) {
        DeleteFileRequest req = new DeleteFileRequest();
        req.setPath(str);
        return req;
    }
}
