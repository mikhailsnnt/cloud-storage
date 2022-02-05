package com.sainnt.server.service.operations;

import com.sainnt.server.entity.File;
import com.sainnt.server.exception.InternalServerError;
import com.sainnt.server.util.ApplicationUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class FileDownloadOperation implements ByteDownloadOperation{
    private final long size;
    private final java.io.File path ;

    public FileDownloadOperation(File file){
        size = file.getSize();
        path =Path.of(ApplicationUtils.getPath(file.getId())).toFile();
        try{
            if(path.length()!=file.getSize())
            {
                log.error("File actual size mismatch entity data");
                throw new InternalServerError();
            }
        }catch (Exception e){
            log.error("File size check exception");
            throw new InternalServerError();
        }
    }
    @Override
    public long getSize() {
        return size;
    }


    @Override
    public FileRegion getFileRegion() {
        return new DefaultFileRegion(path, 0,size);
    }


}

