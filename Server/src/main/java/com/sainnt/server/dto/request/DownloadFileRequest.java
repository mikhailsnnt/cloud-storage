package com.sainnt.server.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DownloadFileRequest extends Request {
    private String path;
}
