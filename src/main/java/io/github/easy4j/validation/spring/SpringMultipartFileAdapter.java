package io.github.easy4j.validation.spring;

import io.github.easy4j.validation.file.UploadFile;
import io.github.easy4j.validation.file.UploadFileAdapter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/** Adapts a Spring {@link MultipartFile} to the common {@link UploadFile} contract. */
public final class SpringMultipartFileAdapter implements UploadFileAdapter {

    @Override
    public boolean supports(Object value) {
        return value instanceof MultipartFile;
    }

    @Override
    public UploadFile adapt(Object value) {
        return new SpringUploadFile((MultipartFile) value);
    }

    private static final class SpringUploadFile implements UploadFile {

        private final MultipartFile multipartFile;

        private SpringUploadFile(MultipartFile multipartFile) {
            this.multipartFile = multipartFile;
        }

        @Override public String getName() { return multipartFile.getName(); }
        @Override public String getOriginalFilename() { return multipartFile.getOriginalFilename(); }
        @Override public String getContentType() { return multipartFile.getContentType(); }
        @Override public boolean isEmpty() { return multipartFile.isEmpty(); }
        @Override public long getSize() { return multipartFile.getSize(); }
        @Override public byte[] getBytes() throws IOException { return multipartFile.getBytes(); }
        @Override public InputStream getInputStream() throws IOException { return multipartFile.getInputStream(); }
    }
}
