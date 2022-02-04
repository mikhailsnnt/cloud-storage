package com.sainnt.server.service.operations;

import com.sainnt.server.entity.File;
import com.sainnt.server.exception.InternalServerError;
import com.sainnt.server.util.ApplicationUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class FileDownloadOperation implements ByteDownloadOperation{
    private final MessageDigest md;
    private BufferedInputStream bufReadStream;
    private final long size;
    private final Path path ;
    private  long bytesRead;
    private final int bufferSize = 4096;
    private boolean initiated = false;

    public FileDownloadOperation(File file){
        size = file.getSize();
        bytesRead = 0;
        path =Path.of(ApplicationUtils.getPath(file.getId()));
        try{
        if(Files.size(path)!=file.getSize())
        {
            log.error("File actual size mismatch entity data");
             throw new InternalServerError();
        }
        }catch (Exception e){
            log.error("File size check exception");
            throw new InternalServerError();
        }
        try{
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("Getting MD5 algorithm exception",e);
            throw  new InternalServerError();
        }
    }
    @Override
    public long getSize() {
        return size;
    }

    @Override
    public ByteBuf getBytePortion() {
        if(!initiated)
            initChannel();
        int bytesToRead = (int) Math.min(size - bytesRead, bufferSize);
        ByteBuf portion = PooledByteBufAllocator.DEFAULT.buffer(bytesToRead);
        try {
            portion.writeBytes(bufReadStream, bytesToRead);
            bytesRead += bytesToRead;
        }catch (IOException e){
            log.error("IOException loading file",e);
            throw new InternalServerError();
        }
        initiated = true;
        return portion;
    }

    @Override
    public byte[] getCheckSum() {
        closeChannel();
        return md.digest();
    }

    @Override
    public boolean bytesLeft() {
        if(!initiated)
            initChannel();
        return bufReadStream!=null && (bytesRead < size);
    }

    @Override
    public void cancel() {
        closeChannel();
    }

    public void initChannel(){
        try{
            bufReadStream = new BufferedInputStream(
                    new DigestInputStream(
                            new FileInputStream(path.toFile()),md));
        } catch (FileNotFoundException e) {
            log.error("Local file not found",e);
            throw new InternalServerError();
        }
    }
    public void closeChannel(){
        try{
            bufReadStream.close();
        }
        catch (IOException e){
            log.error("Exception closing loading file stream",e);
            throw  new InternalServerError();
        }

    }
}

