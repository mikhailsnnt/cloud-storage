package com.sainnt.net;

import com.sainnt.files.RemoteFileRepresentation;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OperationHandler extends ByteToMessageDecoder {
    int operationCode = -1;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (operationCode == -1){
        if(byteBuf.readableBytes() < 4 )
            return;
        int responseCode = byteBuf.readInt();

        System.out.println("Response: "+responseCode);
        if(responseCode == 113)
            operationCode = responseCode;
        }
        else {
//            if(operationCode == 113){
//                byteBuf.markReaderIndex();
//                if(byteBuf.readableBytes() <4){
//                    byteBuf.resetReaderIndex();
//                    return;
//                }
//                int pathSize = byteBuf.readInt();
//                if(byteBuf.readableBytes() < pathSize)
//                {
//                    byteBuf.resetReaderIndex();
//                    return;
//                }
//                String path = byteBuf.readCharSequence(pathSize,StandardCharsets.UTF_8).toString();
//                int filesCount = byteBuf.readInt();
//                List<RemoteFileRepresentation> ls = new ArrayList<>();
//                for (int i = 0; i < filesCount; i++) {
//                    if(byteBuf.readableBytes()<6){
//                        ls.clear();
//                        byteBuf.resetReaderIndex();
//                        return;
//                    }
//                    boolean isDir = byteBuf.readBoolean();
//                    boolean completed = byteBuf.readBoolean();
//                    int nameSize = byteBuf.readInt();
//                    if(byteBuf.readableBytes()<nameSize)
//                    {
//                        ls.clear();
//                        byteBuf.resetReaderIndex();
//                        return;
//                    }
//                    if(!isDir){
//                        if(byteBuf.readableBytes()<8)
//                        {
//                            ls.clear();
//                            byteBuf.resetReaderIndex();
//                            return;
//                        }
//                        byteBuf.readLong();
//                        ls.add(new RemoteFileRepresentation("",byteBuf.readCharSequence(nameSize,StandardCharsets.UTF_8).toString(),
//                                false));
//                    }
//                    else ls.add(new RemoteFileRepresentation("",
//                            byteBuf.readCharSequence(nameSize, StandardCharsets.UTF_8).toString(),
//                            true
//                    ));
//                }
//                System.out.printf("File response for %s\n",path);
//                ls.forEach(t-> System.out.printf("%s %s\n",t.getName(),t.isDirectory()));
//            }
        }
    }
}
