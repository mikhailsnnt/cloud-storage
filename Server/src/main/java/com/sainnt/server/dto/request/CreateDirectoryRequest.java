package com.sainnt.server.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateDirectoryRequest extends Request {
    private String path;
}
