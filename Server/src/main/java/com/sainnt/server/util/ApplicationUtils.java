package com.sainnt.server.util;

public class ApplicationUtils {
    private ApplicationUtils() {
    }

    public static String getPath(long fid) {
        return "files/" + fid;
    }
}
