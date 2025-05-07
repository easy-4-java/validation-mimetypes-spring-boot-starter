package com.github.hiwepy.validation.provider;

import org.springframework.web.multipart.MultipartFile;

public interface FileContentCheckProvider {

    Boolean check(MultipartFile multipartFile);

    String support();

}
