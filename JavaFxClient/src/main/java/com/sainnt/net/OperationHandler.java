package com.sainnt.net;

import com.sainnt.dto.ExceptionDto;
import com.sainnt.files.FileRepresentation;
import com.sainnt.files.RemoteFileRepresentation;
import com.sainnt.util.CodesInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OperationHandler extends ByteToMessageDecoder {
    private final CloudClient client;
    private  Consumer<ExceptionDto> exceptionInfoConsumer;

    public void setExceptionDescriptionConsumer(Consumer<ExceptionDto> exceptionInfoConsumer) {
        this.exceptionInfoConsumer = exceptionInfoConsumer;
    }

    public OperationHandler() {
        this.client = CloudClient.getClient();
    }

    public OperationHandler(Consumer<ExceptionDto> exceptionInfoConsumer) {
        this();
        this.exceptionInfoConsumer = exceptionInfoConsumer;
    }

    int operationCode = -1;
    private int exceptionCode = -1;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (operationCode == -1) {
            if (byteBuf.readableBytes() < 4)
                return;
            int responseCode = byteBuf.readInt();
            System.out.println("Response: " + responseCode);
            if (CodesInfo.isExceptionCode(responseCode))
                exceptionCode = responseCode;
            else if (responseCode == 113 || responseCode == 8) {
                operationCode = responseCode;
            }
        }
        if (exceptionCode != -1) {
            if (byteBuf.readableBytes() < 4)
                return;
            byteBuf.markReaderIndex();
            int exceptionDescSize = byteBuf.readInt();
            if (byteBuf.readableBytes() < exceptionDescSize) {
                byteBuf.resetReaderIndex();
                return;
            }
            String desc = byteBuf.readCharSequence(exceptionDescSize, StandardCharsets.UTF_8).toString();
            exceptionInfoConsumer.accept(new ExceptionDto(CodesInfo.getExceptionDescription(exceptionCode), desc));
            operationCode = -1;
            exceptionCode = -1;
        }
        if (operationCode == 113)
            handleFilesListRequest(byteBuf);
        else if (operationCode == 8)
            handleFileUpload(byteBuf);

    }


    private void handleFileUpload(ByteBuf byteBuf) {
        byteBuf.markReaderIndex();
        if(byteBuf.readableBytes()<4)
        {
            byteBuf.resetReaderIndex();
            return;
        }
        int pathSize = byteBuf.readInt();
        if(byteBuf.readableBytes()<pathSize)
        {
            byteBuf.resetReaderIndex();
            return;
        }
        String path = byteBuf.readCharSequence(pathSize,StandardCharsets.UTF_8).toString();
        client.handleFileUploadResponse(path);
        operationCode = -1;
    }

    private void handleFilesListRequest(ByteBuf byteBuf){
        byteBuf.markReaderIndex();
            if(byteBuf.readableBytes() <4){
                byteBuf.resetReaderIndex();
                return;
            }
            int pathSize = byteBuf.readInt();
            if(byteBuf.readableBytes() < pathSize)
            {
                byteBuf.resetReaderIndex();
                return;
            }
            String path = byteBuf.readCharSequence(pathSize,StandardCharsets.UTF_8).toString();
            if(byteBuf.readableBytes()<4)
            {
                byteBuf.resetReaderIndex();
                return;
            }
            int filesCount = byteBuf.readInt();
            List<FileRepresentation> ls = new ArrayList<>();
            for (int i = 0; i < filesCount; i++) {
                if(byteBuf.readableBytes()<6){
                    ls.clear();
                    byteBuf.resetReaderIndex();
                    return;
                }
                boolean isDir = byteBuf.readBoolean();
                boolean completed = byteBuf.readBoolean();
                int nameSize = byteBuf.readInt();
                if(byteBuf.readableBytes()<nameSize)
                {
                    ls.clear();
                    byteBuf.resetReaderIndex();
                    return;
                }
                String filename = byteBuf.readCharSequence(nameSize, StandardCharsets.UTF_8).toString();
                if(!isDir){
                    if(byteBuf.readableBytes()<8)
                    {
                        ls.clear();
                        byteBuf.resetReaderIndex();
                        return;
                    }
                    byteBuf.readLong();
                    ls.add(new RemoteFileRepresentation(path, filename,
                            false));
                }
                else
                    ls.add(new RemoteFileRepresentation(path,
                            filename,
                            true
                    ));
            }
            client.handleFilesRequest(path,ls);
            operationCode = -1;
    }
}
