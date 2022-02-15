package com.sainnt.server.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FilesListRequest extends Request {
    private long id;
}
