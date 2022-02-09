package com.sainnt.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CodesInfo {
    private CodesInfo(){}
    private  static final Map<Integer, String>  exceptionDescriptions;
    static {
        exceptionDescriptions = new HashMap<>();
        exceptionDescriptions.put(200, "Access denied");
        exceptionDescriptions.put(201, "Checksum mismatch");
        exceptionDescriptions.put(202, "Directory already exists");
        exceptionDescriptions.put(203, "Directory not found");
        exceptionDescriptions.put(204, "File already exists");
        exceptionDescriptions.put(205, "File not found");
        exceptionDescriptions.put(206, "Internal server error");
        exceptionDescriptions.put(207, "Invalid filename");
    }
    public static String getExceptionDescription(int code){
        return exceptionDescriptions.get(code);
    }
    public static boolean isExceptionCode(int code){
        return exceptionDescriptions.containsKey(code);
    }
}
