package com.sainnt.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class RenameDirectoryRequest extends Request{
    private long id;
    private String newName;
}
