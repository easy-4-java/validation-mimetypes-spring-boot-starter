package com.github.hiwepy.validation.provider;

import org.springframework.web.multipart.MultipartFile;

public interface FileContentCheckProvider {

    /**
     * 检查文件内容是否合法
     * @param multipartFile
     * @return
     */
    Boolean check(MultipartFile multipartFile);

    /**
     * 支持的文件后缀，例如：txt,doc,docx,xls,xlsx,pdf
     * @return
     */
    String support();

}
