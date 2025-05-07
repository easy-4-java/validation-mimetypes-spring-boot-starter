package com.github.hiwepy.validation;

import com.github.hiwepy.validation.provider.FileContentCheckProvider;
import com.github.hiwepy.validation.provider.FileContentCheckStrategy;
import org.apache.tika.Tika;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Configuration
@ConditionalOnClass({Tika.class})
public class MimeTypeValidationAutoConfiguration {

    @Bean
    public FileContentCheckStrategy fileContentCheckStrategy(ObjectProvider<FileContentCheckProvider> fileContentCheckProviders) {
        return new FileContentCheckStrategy(fileContentCheckProviders.stream().collect(Collectors.toList()));
    }

    @Bean
    @ConditionalOnMissingBean
    public FileContentCheckProvider fileContentCheckProvider() {
        return new FileContentCheckProvider() {
            @Override
            public Boolean check(MultipartFile multipartFile) {
                return Boolean.TRUE;
            }

            @Override
            public String support() {
                return "*/*";
            }
        };
    }

}
