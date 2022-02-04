package com.sainnt.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDto {
    private String name;
    private boolean isDirectory;
    private long size;
    private boolean completed;
}
