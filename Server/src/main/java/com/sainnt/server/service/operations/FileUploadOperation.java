package com.sainnt.server.service.operations;

import com.sainnt.server.dao.DaoException;
import com.sainnt.server.dao.FileRepository;
import com.sainnt.server.entity.File;
import com.sainnt.server.exception.InternalServerError;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class FileUploadOperation implements ByteUploadOperation {
    private BufferedOutputStream writeStream;
    private final File file;
    private long bytesRead;
    private final FileRepository fileRepository;

    public FileUploadOperation(File file, FileRepository fileRepository) {
        this.file = file;
        this.fileRepository = fileRepository;
        initiateWriteStream();
    }

    private void initiateWriteStream() {
        try {
            bytesRead = 0;
            writeStream = new BufferedOutputStream(new FileOutputStream("files/" + file.getId()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean transferBytesFromByteBuf(ByteBuf buf) {
        int bytesToTransfer = (int) Math.min(buf.readableBytes(), file.getSize() - bytesRead);
        try {
            buf.readBytes(writeStream, bytesToTransfer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        bytesRead += bytesToTransfer;
        if (file.getSize() == bytesRead) {
            closeStream();
//            if(!Arrays.equals(getCheckSum(), checkSum)) {
//                throw new CheckSumMismatchException(file.getName());
//            }
            completeFileUploadOperation();
            return true;
        }
        return false;
    }

    private void closeStream() {
        try {
            writeStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void interrupt() {
        closeStream();
        log.info("File upload operation interrupted at {} of {} bytes", bytesRead, file.getSize());
    }

    @Override
    public long getFileId() {
        return file.getId();
    }

    public void completeFileUploadOperation() {
        file.setComplete(true);
        try {
            fileRepository.updateFile(file);
        } catch (DaoException e) {
            log.error("Error completing file upload", e);
            throw new InternalServerError();
        }
    }
}
