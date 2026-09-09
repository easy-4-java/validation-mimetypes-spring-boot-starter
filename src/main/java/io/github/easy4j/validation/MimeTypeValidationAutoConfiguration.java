package io.github.easy4j.validation;

import io.github.easy4j.validation.provider.FileContentCheckProvider;
import io.github.easy4j.validation.provider.FileContentCheckStrategy;
import org.apache.tika.Tika;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/** Spring Boot auto-configuration for MIME type validation. */
@AutoConfiguration
@ConditionalOnClass(Tika.class)
public class MimeTypeValidationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FileContentCheckStrategy fileContentCheckStrategy(
            ObjectProvider<FileContentCheckProvider> providers) {
        List<FileContentCheckProvider> contentCheckProviders = providers.orderedStream().toList();
        return new FileContentCheckStrategy(contentCheckProviders);
    }
}
