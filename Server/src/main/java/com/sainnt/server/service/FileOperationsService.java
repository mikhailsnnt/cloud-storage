package com.sainnt.server.service;

import com.sainnt.server.dto.request.UploadFileRequest;
import com.sainnt.server.service.operations.FileUploadOperation;

public interface FileOperationsService {
    FileUploadOperation uploadFile(UploadFileRequest request);
}
