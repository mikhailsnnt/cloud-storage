package com.sainnt.dto;

import javafx.scene.input.DataFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RemoteFileDto implements Serializable {
    public static final DataFormat dataFormat = new DataFormat("com.sainnt.dto.RemoteFileDto");
    private String name;
    private long id;
}
