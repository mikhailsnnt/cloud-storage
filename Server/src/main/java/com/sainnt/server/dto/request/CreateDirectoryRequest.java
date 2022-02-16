package com.sainnt.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CreateDirectoryRequest extends Request {
    private long parentId;
    private String name;
}
