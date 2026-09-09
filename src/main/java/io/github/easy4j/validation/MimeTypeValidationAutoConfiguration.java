package io.github.easy4j.validation;

import io.github.easy4j.validation.provider.FileContentCheckProvider;
import io.github.easy4j.validation.provider.FileContentCheckStrategy;
import org.apache.tika.Tika;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/** Spring Boot 2 auto-configuration for MIME type validation. */
@Configuration
@ConditionalOnClass(Tika.class)
public class MimeTypeValidationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FileContentCheckStrategy fileContentCheckStrategy(
            ObjectProvider<FileContentCheckProvider> providers) {
        List<FileContentCheckProvider> contentCheckProviders =
                providers.orderedStream().collect(Collectors.toList());
        return new FileContentCheckStrategy(contentCheckProviders);
    }
}
