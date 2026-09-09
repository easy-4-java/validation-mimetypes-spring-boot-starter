package io.github.easy4j.validation;

import io.github.easy4j.validation.constraints.FileNotEmpty;
import io.github.easy4j.validation.provider.FileContentCheckStrategy;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileNotEmptyIntegrationTest {

    private final Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();

    @Test
    void shouldValidateSpringMultipartFileThroughCommonConstraint() {
        UploadCommand valid = new UploadCommand(file("report.pdf", "%PDF-1.7\n%%EOF"));
        UploadCommand renamedExecutable = new UploadCommand(file("report.pdf", "MZ executable"));

        assertTrue(validator.validate(valid).isEmpty());
        assertEquals(1, validator.validate(renamedExecutable).size());
    }

    @Test
    void shouldValidateMultipleFilesAndRequiredRules() {
        MultipartFile pdf = file("report.pdf", "%PDF-1.7\n%%EOF");
        MultipartFile executable = file("report.pdf", "MZ executable");

        assertTrue(validator.validate(new MultipleUpload(new MultipartFile[] {pdf, pdf})).isEmpty());
        assertEquals(1, validator.validate(new MultipleUpload(new MultipartFile[] {pdf, executable})).size());
        assertEquals(1, validator.validate(new MultipleUpload(new MultipartFile[0])).size());
        assertTrue(validator.validate(new OptionalUpload(null)).isEmpty());
    }

    @Test
    void shouldAutoConfigureCommonContentCheckStrategy() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MimeTypeValidationAutoConfiguration.class))
                .run(context -> assertTrue(context.containsBean("fileContentCheckStrategy")
                        && context.getBean(FileContentCheckStrategy.class) != null));
    }

    private MultipartFile file(String name, String content) {
        return new MockMultipartFile("file", name, "application/pdf",
                content.getBytes(StandardCharsets.US_ASCII));
    }

    private static final class UploadCommand {

        @FileNotEmpty(extensions = "pdf", mimeTypes = "application/pdf", strict = true)
        private final MultipartFile file;

        private UploadCommand(MultipartFile file) {
            this.file = file;
        }
    }

    private static final class MultipleUpload {

        @FileNotEmpty(extensions = "pdf", mimeTypes = "application/pdf", strict = true)
        private final MultipartFile[] files;

        private MultipleUpload(MultipartFile[] files) {
            this.files = files;
        }
    }

    private static final class OptionalUpload {

        @FileNotEmpty(required = false)
        private final MultipartFile[] files;

        private OptionalUpload(MultipartFile[] files) {
            this.files = files;
        }
    }
}
