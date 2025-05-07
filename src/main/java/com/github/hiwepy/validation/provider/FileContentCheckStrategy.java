package com.github.hiwepy.validation.provider;

import org.apache.tika.mime.MimeType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FileContentCheckStrategy implements InitializingBean {

    private final List<FileContentCheckProvider> fileContentCheckProviders;

    public FileContentCheckStrategy(List<FileContentCheckProvider> fileContentCheckProviders) {
        this.fileContentCheckProviders = fileContentCheckProviders;
    }

    private Map<String, FileContentCheckProvider> fileContentCheckProviderMap;

    public Boolean check(MimeType detectMimeType, MultipartFile multipartFile) {
        FileContentCheckProvider fileContentCheckProvider = fileContentCheckProviderMap.get(detectMimeType.getName());
        if (fileContentCheckProvider != null) {
            return fileContentCheckProvider.check(multipartFile);
        }
        return Boolean.TRUE;
    }

    public Boolean check(String mime, MultipartFile multipartFile) {
        FileContentCheckProvider fileContentCheckProvider = fileContentCheckProviderMap.get(mime);
        if (fileContentCheckProvider != null) {
            return fileContentCheckProvider.check(multipartFile);
        }
        return Boolean.TRUE;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        fileContentCheckProviderMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(fileContentCheckProviders)) {
            for (FileContentCheckProvider fileContentCheckProvider : fileContentCheckProviders) {
                fileContentCheckProviderMap.put(fileContentCheckProvider.support(), fileContentCheckProvider);
            }
        }
    }
}
