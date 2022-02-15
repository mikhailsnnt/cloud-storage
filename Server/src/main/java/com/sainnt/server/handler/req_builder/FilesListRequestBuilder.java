package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.FilesListRequest;
import com.sainnt.server.dto.request.Request;

public class FilesListRequestBuilder extends IdRequestBuilder {
    @Override
    protected Request formRequest(long id) {
        FilesListRequest request = new FilesListRequest();
        request.setId(id);
        return request;
    }
}
