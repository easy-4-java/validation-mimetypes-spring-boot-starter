package io.github.easy4j.validation.spring;

import io.github.easy4j.validation.file.UploadFile;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringMultipartFileAdapterTest {

    @Test
    void shouldAdaptAndDelegateMultipartFileContract() throws Exception {
        byte[] content = "%PDF-1.7\n%%EOF".getBytes(StandardCharsets.US_ASCII);
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "report.pdf", "application/pdf", content);
        SpringMultipartFileAdapter adapter = new SpringMultipartFileAdapter();

        assertTrue(adapter.supports(multipartFile));
        assertFalse(adapter.supports(null));
        assertFalse(adapter.supports("not-a-file"));

        UploadFile uploadFile = adapter.adapt(multipartFile);
        assertEquals("file", uploadFile.getName());
        assertEquals("report.pdf", uploadFile.getOriginalFilename());
        assertEquals("application/pdf", uploadFile.getContentType());
        assertFalse(uploadFile.isEmpty());
        assertEquals(content.length, uploadFile.getSize());
        assertArrayEquals(content, uploadFile.getBytes());
        assertEquals('%', uploadFile.getInputStream().read());
    }
}
