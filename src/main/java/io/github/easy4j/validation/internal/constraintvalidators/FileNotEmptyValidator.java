package io.github.easy4j.validation.internal.constraintvalidators;

import io.github.easy4j.validation.MimeTypeDetectorHolder;
import io.github.easy4j.validation.constraints.FileNotEmpty;
import io.github.easy4j.validation.utils.FilemimeUtils;
import io.github.easy4j.validation.utils.FiletypeUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.overviewproject.mime_types.GetBytesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 单文件校验
 * @author wandl
 * @version 1.0
 * @since 2022.11.07
 */
public class FileNotEmptyValidator implements ConstraintValidator<FileNotEmpty, MultipartFile> {

    private Logger log = LoggerFactory.getLogger(FileNotEmptyValidator.class);
    private Set<String> extensionSet = new HashSet<>();
    private Set<String> mimeTypeSet = new HashSet<>();
    private DataSize maxSize;
    private boolean required;

    @Override
    public void initialize(FileNotEmpty annotation) {
        this.extensionSet = ArrayUtils.isNotEmpty(annotation.extensions()) ? Stream.of(annotation.extensions()).map(ext -> ext.toLowerCase()).collect(Collectors.toSet()) : Collections.emptySet();
        this.mimeTypeSet = ArrayUtils.isNotEmpty(annotation.mimeTypes()) ?  Stream.of(annotation.mimeTypes()).map(mime -> mime.toLowerCase()).collect(Collectors.toSet()) : Collections.emptySet();
        this.required = annotation.required();
        this.maxSize = StringUtils.hasText(annotation.maxSize()) ? DataSize.parse(annotation.maxSize(), DataUnit.BYTES): null;
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {

        // 1、验证文件是否为空
        if (Objects.isNull(multipartFile) || multipartFile.isEmpty()) {
            return !required;
        }
        // 2、验证文件大小是否满足要求
        if (Objects.nonNull(maxSize) && maxSize.compareTo(DataSize.of(multipartFile.getSize(), DataUnit.BYTES)) <= 0) {
            return Boolean.FALSE;
        }
        // 3、验证文件后缀是否满足要求
        if (!extensionSet.isEmpty()) {
            String detectExtension = null;
            try {
                detectExtension = FiletypeUtils.getFileType(multipartFile.getInputStream());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if(!StringUtils.hasText(detectExtension)){
                detectExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            }
            if(!StringUtils.hasText(detectExtension)){
                return Boolean.FALSE;
            }
            if(!extensionSet.contains(detectExtension.toLowerCase())){
                return Boolean.FALSE;
            }
        }
        // 4、验证文件 content type 是否满足要求
        if (!mimeTypeSet.isEmpty()) {
            String detectMimeType = null;
            try {
                detectMimeType = MimeTypeDetectorHolder.instance().getDetector().detectMimeType(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Boolean.FALSE;
            }
            if(!StringUtils.hasText(detectMimeType)){
                return Boolean.FALSE;
            }
            if(!mimeTypeSet.contains(detectMimeType.toLowerCase())){
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
