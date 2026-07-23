package io.github.easy4j.validation.constraints;

import io.github.easy4j.validation.internal.constraintvalidators.FileNotEmptyValidator;
import io.github.easy4j.validation.internal.constraintvalidators.FilesNotEmptyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * jsr303 文件格式校验注解
 *
 * @author wandl
 * @version 1.0
 * @since 2022.11.07
 */
@Documented
@Constraint(
    validatedBy = {FileNotEmptyValidator.class, FilesNotEmptyValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileNotEmpty {

    /**
     * Message string.
     *
     * @return the string
     */
    String message() default "文件格式不正确";

    /**
     * 校验组
     *
     * @return the class [ ]
     */
    Class<?>[] groups() default {};

    /**
     * Payload class [ ].
     *
     * @return the class [ ]
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 需要校验的格式数组
     * @return the extension the annotated MultipartFile must match, e.g. pdf . Per default extension is allowed
     */
    String[] extensions() default {};

    /**
     * 需要校验的 mime type 数组
     *
     * @return the mime type the annotated MultipartFile must match, e.g. application/pdf. Per default any mime type  is allowed
     */
    String[] mimeTypes() default {};

    /**
     * 允许的文件大小，e.g. 2MB；格式参加
     * @see org.springframework.util.unit.DataUnit
     * @return the maxsize the annotated MultipartFile must limit, e.g. 2M . Per default maxsize is not limit
     */
    String maxSize() default "2MB";

    /**
     * 是否必填 为 false时文件为空则不校验格式，不为空则校验格式 为true时文件不能为空且需要验证格式
     * @return the boolean
     */
    boolean required() default true;

}
