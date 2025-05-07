/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.hiwepy.validation;

import jakarta.activation.MimetypesFileTypeMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * TODO
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */

public class Test {
    /**
     * 默认的MimeTypes
     */
    private static final MimeTypes DEFAULT_MIME_TYPES = MimeTypes.getDefaultMimeTypes();

    private static volatile Tika tika = new Tika();

    public static void main(String[] args) throws Exception {

        Resource resource = new ClassPathResource("aaaa.pdf");
        String detectedMediaType = tika.detect(resource.getInputStream());
        MimeType detectMimeType =  DEFAULT_MIME_TYPES.forName(detectedMediaType);
        System.out.println("文件类型为：" + detectMimeType.getAcronym());
        System.out.println("文件类型为：" + detectMimeType.getType());
        System.out.println("文件扩展名为：" + detectMimeType.getExtension());
        System.out.println("文件扩展名为：" + FilenameUtils.getExtension(detectMimeType.getExtension()));
        detectMimeType.getExtensions().forEach(System.out::println);

        String type = new MimetypesFileTypeMap().getContentType("aaaa.pdf");//name:"aa.txt"
        System.out.println("文件类型为：" + type);
    }

}
