package com.sainnt.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDto {
    private long id;
    private String name;
    private boolean isDirectory;
    private long size;
    private boolean completed;
}
