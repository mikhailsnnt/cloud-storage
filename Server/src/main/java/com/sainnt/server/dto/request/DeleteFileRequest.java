package com.sainnt.server.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteFileRequest extends Request{
    private String path;
}
