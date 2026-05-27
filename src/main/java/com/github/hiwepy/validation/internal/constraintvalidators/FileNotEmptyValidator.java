package io.github.hiwepy.validation.internal.constraintvalidators;

import io.github.hiwepy.validation.constraints.FileNotEmpty;
import io.github.hiwepy.validation.provider.FileContentCheckStrategy;
import io.github.hiwepy.validation.utils.MimetypeUtil;
import io.github.hiwepy.validation.utils.TikaUtil;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 单文件校验
 * @author wandl
 * @version 1.0
 * @since 2022.11.07
 */
@Slf4j
public class FileNotEmptyValidator implements ConstraintValidator<FileNotEmpty, MultipartFile> {

    private Set<String> extensionSet = new HashSet<>();
    private Set<String> mimeTypeSet = new HashSet<>();
    private DataSize maxSize;
    private boolean required;
    private boolean strict;

    @Autowired
    private FileContentCheckStrategy contentCheckStrategy;

    @Override
    public void initialize(FileNotEmpty annotation) {
        this.extensionSet = ArrayUtils.isNotEmpty(annotation.extensions()) ? Stream.of(annotation.extensions()).map(ext -> ext.toLowerCase()).collect(Collectors.toSet()) : Collections.emptySet();
        this.mimeTypeSet = ArrayUtils.isNotEmpty(annotation.mimeTypes()) ?  Stream.of(annotation.mimeTypes()).map(mime -> mime.toLowerCase()).collect(Collectors.toSet()) : Collections.emptySet();
        this.required = annotation.required();
        this.strict = annotation.strict();
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
        // 3、验证文件后缀和 content type 是否满足要求
        if(extensionSet.isEmpty() && mimeTypeSet.isEmpty()){
            return Boolean.TRUE;
        }
        try {
            // 4、判断是否严格模式
            if(strict) {
                // 4.1、首先尝试使用Apache Tika 解析文件类型
                MimeType detectMimeType = TikaUtil.detectMimeType(multipartFile.getInputStream());
                if(Objects.nonNull(detectMimeType)
                        && StringUtils.hasText(detectMimeType.getExtension())
                        && StringUtils.hasText(detectMimeType.getName())){
                    String extension = FilenameUtils.getExtension(detectMimeType.getExtension());
                    // 4.1.1、验证文件后缀是否满足要求
                    if (!extensionSet.isEmpty()) {
                        if(!extensionSet.contains(extension.toLowerCase())){
                            return Boolean.FALSE;
                        }
                    }
                    // 4.1.2、验证文件 content type 是否满足要求
                    if (!mimeTypeSet.isEmpty()) {
                        if(!mimeTypeSet.contains(detectMimeType.getName().toLowerCase())){
                            return Boolean.FALSE;
                        }
                    }
                    // 4.1.3、验证文件内容
                    if(Objects.nonNull(contentCheckStrategy) && !contentCheckStrategy.check(extension, multipartFile)){
                        return Boolean.FALSE;
                    }
                    // 5、验证通过
                    return Boolean.TRUE;
                }
                // Apache Tika 解析为空，则会继续执行后续逻辑
            }
            // 4.2、使用 MimetypesFileTypeMap 解析文件类型
            String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            // 4.2.1、验证文件后缀是否满足要求
            if (!extensionSet.isEmpty() && !extensionSet.contains(extension.toLowerCase())) {
                return Boolean.FALSE;
            }
            // 4.2.2、验证文件 content type 是否满足要求
            if(!mimeTypeSet.isEmpty()){
                String mimeType = MimetypeUtil.detectMimeType(multipartFile.getOriginalFilename());
                if(StringUtils.hasText(mimeType) && !mimeTypeSet.contains(mimeType.toLowerCase())) {
                    return Boolean.FALSE;
                }
            }
            // 4.2.3、验证文件内容
            if(Objects.nonNull(contentCheckStrategy) && !contentCheckStrategy.check(extension, multipartFile)){
                return Boolean.FALSE;
            }
            // 5、验证通过
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }
}
