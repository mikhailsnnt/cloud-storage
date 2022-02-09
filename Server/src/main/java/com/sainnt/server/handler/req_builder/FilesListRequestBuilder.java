package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.FilesListRequest;
import com.sainnt.server.dto.request.Request;

public class FilesListRequestBuilder extends OneStringRequestBuilder {
    @Override
    protected Request formRequest(String str) {
        FilesListRequest request = new FilesListRequest();
        request.setPath(str);
        return request;
    }
}
