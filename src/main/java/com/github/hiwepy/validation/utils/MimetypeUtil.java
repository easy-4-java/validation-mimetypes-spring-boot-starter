package io.github.hiwepy.validation.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import jakarta.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.Objects;

@Slf4j
public class MimetypeUtil {

    private final static MimetypesFileTypeMap FILE_TYPE_MAP =  new MimetypesFileTypeMap();

    public static String detectMimeType(File file) {
        if (Objects.isNull(file) || !file.exists()) {
            return null;
        }
        try {
            return FILE_TYPE_MAP.getContentType(file);
        } catch (Exception e) {
            log.error("Error detecting mime type for file: {}, error: {}", file.getAbsolutePath(), ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public static String detectMimeType(String name) {
        if (Objects.isNull(name)) {
            return null;
        }
        try {
            return FILE_TYPE_MAP.getContentType(name);
        } catch (Exception e) {
            log.error("Error detecting mime type error: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

}
