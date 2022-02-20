package com.robin.msf.util;

import org.apache.commons.io.FilenameUtils;

import java.net.URI;
import java.net.URL;


public class URIUtils {

    public static String getRequestPath(String uri) {
        String path=uri;
        int pos = uri.indexOf("?");
        if (pos != -1) {
            path = path.substring(0, pos);
        }
        return path;
    }
    public static String getRequestPath(URI uri) {
        String contentPath = uri.getPath();
        int pos = contentPath.indexOf("?");
        if (pos != -1) {
            contentPath = contentPath.substring(0, pos);
        }
        return contentPath;
    }
    public static String getPathFileName(String requestPath){
        return FilenameUtils.getName(requestPath);
    }

    public static String getRequestRelativePathOrSuffix(String requestPath,String contentPath){
        if (!"/".equals(requestPath)) {
            int pos = contentPath.indexOf(contentPath);
            contentPath = contentPath.substring(pos + contentPath.length());
        }
        String resourcePath = requestPath;
        int pos = contentPath.length();

        resourcePath = resourcePath.substring(pos);
        pos = resourcePath.lastIndexOf("?");
        if (pos != -1) {
            resourcePath = resourcePath.substring(pos + 1);
            pos = resourcePath.indexOf(".");
            if (pos != -1) {
                resourcePath = resourcePath.substring(0, pos);
            }
        }
        return  resourcePath;
    }

}
