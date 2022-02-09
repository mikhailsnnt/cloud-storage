package com.sainnt.server.dto.request;

import com.sainnt.server.dto.request.Request;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UploadFileRequest extends Request {
    private String path;
    private long fileSize;
//    private byte[] checkSum;
}
