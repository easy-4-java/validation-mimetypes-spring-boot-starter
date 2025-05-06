package com.github.hiwepy.validation.internal.constraintvalidators;

import com.github.hiwepy.validation.constraints.FileNotEmpty;
import com.github.hiwepy.validation.provider.FileContentCheckStrategy;
import com.github.hiwepy.validation.utils.TikaUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tika.mime.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 多文件校验
 * @author wandl
 * @version 1.0
 * @since 2022.11.07
 */
public class FilesNotEmptyValidator implements ConstraintValidator<FileNotEmpty, MultipartFile[]> {

    private Logger log = LoggerFactory.getLogger(FileNotEmptyValidator.class);
    private Set<String> extensionSet = new HashSet<>();
    private Set<String> mimeTypeSet = new HashSet<>();
    private DataSize maxSize;
    private boolean required;


    @Autowired(required = false)
    private FileContentCheckStrategy contentCheckStrategy;

    @Override
    public void initialize(FileNotEmpty annotation) {
        this.extensionSet = ArrayUtils.isNotEmpty(annotation.extensions()) ? Stream.of(annotation.extensions()).map(ext -> ext.toLowerCase()).collect(Collectors.toSet()) : Collections.emptySet();
        this.mimeTypeSet = ArrayUtils.isNotEmpty(annotation.mimeTypes()) ?  Stream.of(annotation.mimeTypes()).map(mime -> mime.toLowerCase()).collect(Collectors.toSet()) : Collections.emptySet();
        this.required = annotation.required();
        this.maxSize = StringUtils.hasText(annotation.maxSize()) ? DataSize.parse(annotation.maxSize(), DataUnit.BYTES): null;
    }

    @Override
    public boolean isValid(MultipartFile[] multipartFiles, ConstraintValidatorContext constraintValidatorContext) {
        // 1、验证文件是否为空
        if (Objects.isNull(multipartFiles) || multipartFiles.length == 0) {
            return !required;
        }
        // 2、验证文件后缀和 content type 是否满足要求
        if(extensionSet.isEmpty() && mimeTypeSet.isEmpty()){
            return Boolean.TRUE;
        }
        // 3、循环验证文件
        for (MultipartFile multipartFile : multipartFiles) {
            // 3.1、验证文件大小是否满足要求
            if (Objects.nonNull(maxSize) && maxSize.compareTo(DataSize.of(multipartFile.getSize(), DataUnit.BYTES)) <= 0) {
                return Boolean.FALSE;
            }
            try {
                // 3.2、解析文件类型
                MimeType detectMimeType = TikaUtils.detectMimeType(multipartFile.getInputStream());
                if(Objects.isNull(detectMimeType)){
                    return Boolean.FALSE;
                }
                // 3.3、验证文件后缀是否满足要求
                if (!extensionSet.isEmpty()) {
                    String extension = FilenameUtils.getExtension(detectMimeType.getExtension());
                    if(!extensionSet.contains(extension.toLowerCase())){
                        return Boolean.FALSE;
                    }
                }
                // 3.4、验证文件 content type 是否满足要求
                if (!mimeTypeSet.isEmpty() && !mimeTypeSet.contains(detectMimeType.getType().toString())) {
                    return Boolean.FALSE;
                }
                // 3.5、验证文件内容
                if(Objects.nonNull(contentCheckStrategy) && !contentCheckStrategy.validate(detectMimeType,multipartFile)){
                    return Boolean.FALSE;
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Boolean.FALSE;
            }
        }
        // 4、验证通过
        return Boolean.TRUE;
    }
}
