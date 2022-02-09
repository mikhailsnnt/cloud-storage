package com.sainnt.server.service;

import com.sainnt.server.dto.FileDto;
import com.sainnt.server.dto.request.DownloadFileRequest;
import com.sainnt.server.dto.request.FilesListRequest;
import com.sainnt.server.dto.request.UploadFileRequest;
import com.sainnt.server.service.operations.ByteDownloadOperation;
import com.sainnt.server.service.operations.ByteUploadOperation;

import java.util.List;

public interface FileOperationsService {
    ByteUploadOperation uploadFile(UploadFileRequest request);

    ByteDownloadOperation downloadFile(DownloadFileRequest request);

    List<FileDto> getFiles(FilesListRequest request);
}
