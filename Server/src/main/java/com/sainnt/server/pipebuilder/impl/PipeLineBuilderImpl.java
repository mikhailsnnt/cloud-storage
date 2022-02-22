package com.sainnt.server.pipebuilder.impl;

import com.sainnt.server.entity.User;
import com.sainnt.server.handler.ExceptionHandler;
import com.sainnt.server.handler.OperationDecoder;
import com.sainnt.server.handler.req_handler.*;
import com.sainnt.server.handler.resp_handler.DownloadOperationResponseHandler;
import com.sainnt.server.handler.resp_handler.FileDtoResponseEncoder;
import com.sainnt.server.handler.resp_handler.FilesListResponseHandler;
import com.sainnt.server.pipebuilder.PipeLineBuilder;
import com.sainnt.server.service.AuthenticationService;
import com.sainnt.server.service.FileOperationsService;
import com.sainnt.server.service.NavigationService;
import io.netty.channel.ChannelPipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PipeLineBuilderImpl implements PipeLineBuilder {
    private final AuthenticationService authService;
    private final FileOperationsService fileService;
    private final NavigationService navigationService;

    @Autowired
    public PipeLineBuilderImpl(AuthenticationService authService, FileOperationsService fileService, NavigationService navigationService) {
        this.authService = authService;
        this.fileService = fileService;
        this.navigationService = navigationService;
    }

    @Override
    public void buildUserPipeLine(ChannelPipeline pipeline, User user) {
        OperationDecoder operationDecoder = new OperationDecoder(user, authService);
        pipeline.addLast(operationDecoder);
        pipeline.addLast(new CreateDirectoryRequestHandler(navigationService));
        pipeline.addLast(new UploadFileRequestHandler(fileService, operationDecoder));
        pipeline.addLast(new DeleteFileRequestHandler(navigationService));
        pipeline.addLast(new DeleteDirectoryRequestHandler(navigationService));
        pipeline.addLast(new FileDtoResponseEncoder());
        pipeline.addLast(new FilesListResponseHandler());
        pipeline.addLast(new FilesListRequestHandler(fileService));
        pipeline.addLast(new DownloadOperationResponseHandler());
        pipeline.addLast(new DownloadFileRequestHandler(fileService));
        pipeline.addLast(new RenameFileRequestHandler(navigationService));
        pipeline.addLast(new RenameDirectoryRequestHandler(navigationService));
        pipeline.addLast(new ExceptionHandler());
    }
}
