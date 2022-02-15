package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.DownloadFileRequest;
import com.sainnt.server.dto.request.Request;

public class DownloadFileRequestBuilder extends IdRequestBuilder {
    @Override
    protected Request formRequest(long id) {
        DownloadFileRequest request = new DownloadFileRequest();
        request.setId(id);
        return request;
    }
}
