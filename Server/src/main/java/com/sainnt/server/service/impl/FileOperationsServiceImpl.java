package com.sainnt.server.service.impl;

import com.sainnt.server.dao.DaoException;
import com.sainnt.server.dao.FileRepository;
import com.sainnt.server.dto.request.UploadFileRequest;
import com.sainnt.server.entity.File;
import com.sainnt.server.service.FileOperationsService;
import com.sainnt.server.service.NavigationService;
import com.sainnt.server.service.operations.FileUploadOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class FileOperationsServiceImpl implements FileOperationsService {
    private final FileRepository repository;
    private final NavigationService navigationService;
    public FileOperationsServiceImpl(FileRepository repository, NavigationService navigationService) {
        this.repository = repository;
        this.navigationService = navigationService;
    }

    @Override
    public FileUploadOperation uploadFile(UploadFileRequest request) {
        File file = navigationService.createFile(request.getPath(),request.getUser());
        file.setSize(request.getFileSize());
        try{
            repository.updateFile(file);
        }catch (DaoException e){
            log.error("Error updating filesize during upload operation",e);
        }
        return new FileUploadOperation(file,request.getCheckSum(),repository);
    }

}
