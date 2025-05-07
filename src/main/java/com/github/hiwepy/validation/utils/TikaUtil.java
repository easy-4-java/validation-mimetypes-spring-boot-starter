package com.github.hiwepy.validation.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
public class TikaUtil {
    /**
     * 默认的MimeTypes
     */
    private static final MimeTypes DEFAULT_MIME_TYPES = MimeTypes.getDefaultMimeTypes();

    private static volatile Tika tika = new Tika();

    public static MimeType detectMimeType(File file) {
        if (Objects.isNull(file) || !file.exists()) {
            return null;
        }
        try {
            String detectedMediaType = tika.detect(file);
            return DEFAULT_MIME_TYPES.forName(detectedMediaType);
        } catch (Exception e) {
            log.error("Error detecting mime type for file: {}, error: {}", file.getAbsolutePath(), ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public static MimeType detectMimeType(InputStream input) {
        if (Objects.isNull(input)) {
            return null;
        }
        try {
            String detectedMediaType = tika.detect(input);
            return DEFAULT_MIME_TYPES.forName(detectedMediaType);
        } catch (Exception e) {
            log.error("Error detecting mime type error: {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

}
