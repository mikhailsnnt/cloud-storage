package com.sainnt.server.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UploadFileRequest extends Request {
    private long parentId;
    private long fileSize;
    private String name;
//    private byte[] checkSum;
}
