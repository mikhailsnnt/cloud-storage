package com.sainnt.server.handler.req_builder;

import com.sainnt.server.dto.request.DownloadFileRequest;
import com.sainnt.server.dto.request.Request;

public class DownloadFileRequestBuilder extends OneStringRequestBuilder{
    @Override
    protected Request formRequest(String str) {
        DownloadFileRequest request = new DownloadFileRequest();
        request.setPath(str);
        return request;
    }
}
