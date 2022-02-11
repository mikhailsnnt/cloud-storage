package com.sainnt.server.dto;

import com.sainnt.server.entity.Directory;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DirectoryWithAccessInfo {
    private Directory directory;
    private boolean userAuthorized;
}
