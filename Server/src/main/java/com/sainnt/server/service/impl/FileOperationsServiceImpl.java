package com.sainnt.server.service.impl;

import com.sainnt.server.dao.DaoException;
import com.sainnt.server.dao.FileRepository;
import com.sainnt.server.dto.FileDto;
import com.sainnt.server.dto.request.DownloadFileRequest;
import com.sainnt.server.dto.request.FilesListRequest;
import com.sainnt.server.dto.request.UploadFileRequest;
import com.sainnt.server.entity.Directory;
import com.sainnt.server.entity.File;
import com.sainnt.server.service.FileOperationsService;
import com.sainnt.server.service.NavigationService;
import com.sainnt.server.service.operations.ByteDownloadOperation;
import com.sainnt.server.service.operations.ByteUploadOperation;
import com.sainnt.server.service.operations.FileDownloadOperation;
import com.sainnt.server.service.operations.FileUploadOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


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
    public ByteUploadOperation uploadFile(UploadFileRequest request) {
        File file = navigationService.createFile(request.getPath(),request.getUser());
        file.setSize(request.getFileSize());
        try{
            repository.updateFile(file);
        }catch (DaoException e){
            log.error("Error updating filesize during upload operation",e);
        }
        return new FileUploadOperation(file,request.getCheckSum(),repository);
    }

    @Override
    public ByteDownloadOperation downloadFile(DownloadFileRequest request) {
        return new FileDownloadOperation(navigationService.getFileByPath(request.getPath(),request.getUser()));
    }

    @Override
    public List<FileDto> getFiles(FilesListRequest request) {
        Directory dir = navigationService.findDirectoryByPath(request.getPath(),request.getUser());
        List<FileDto> list = new ArrayList<>();
        Set<Directory> subDirs = dir.getSubDirs();
        if (subDirs!=null)
            subDirs.stream()
                .map(t->new FileDto(t.getName(),true,0,true))
                .forEach(list::add);
        Set<File> files = dir.getFiles();
        if (files!=null) {
            files
                    .stream()
                    .map(t->new FileDto(t.getName(),false,t.getSize(),t.isComplete()))
                    .forEach(list::add);
        }
        return list;
    }

}
