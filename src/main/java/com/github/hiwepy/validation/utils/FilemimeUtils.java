package com.github.hiwepy.validation.utils;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;


@Slf4j
public abstract class FilemimeUtils {

	protected static final String MIMETYPES_PROPERTIES = "mimeTypes.properties";
	protected static final String DEFAULT_MIME = "application/octet-stream";
	protected static Properties properties;

	static{
		try {
			properties = new Properties();
			// 创建 ClassPathResource 实例
			Resource resource = new ClassPathResource(MIMETYPES_PROPERTIES);
			// 检查资源是否存在
			if (resource.exists()) {
				// 获取资源的输入流
				try (InputStream inputStream = resource.getInputStream()) {
					// 读取资源内容
					properties.load(inputStream);
				}
			} else {
				log.warn("Resource " + MIMETYPES_PROPERTIES + " not found in classpath.");
			}
		} catch (Exception e) {
			log.error(MIMETYPES_PROPERTIES + " load error: ", e);
		}
	}

	public static String getFileMimeType(File file) {
		if (file == null) {
			return DEFAULT_MIME;
		}
		return getFileMimeType(file.getName());
	}

	public static String getFileMimeType(String fileName) {
		if ((!StringUtils.hasText(fileName)) || (fileName.indexOf(".") == -1)) {
			return DEFAULT_MIME;
		}
		fileName = fileName.substring(fileName.lastIndexOf("."));
		return getExtensionMimeType(fileName);
	}

	public static String getExtensionMimeType(String extension) {
		String result = null;
		if (!StringUtils.hasText(extension)) {
			return result;
		}
		extension = extension.toLowerCase();
		if (!(extension.startsWith("."))) {
			extension = "." + extension;
		}
		result = (String) properties.getProperty(extension, DEFAULT_MIME);
		return result;
	}

	public static void main(String[] args) {
		System.out.println("FileMimeUtils.getExtensionMimeType(gif)=" + getExtensionMimeType("gif"));
		System.out.println("FileMimeUtils.getExtensionMimeType(.pdf)=" + getExtensionMimeType(".pdf"));
		System.out.println("FileMimeUtils.getExtensionMimeType(.xls)=" + getExtensionMimeType(".xls"));
		System.out.println("FileMimeUtils.getFileMimeType(foo.gif)=" + getFileMimeType("foo.gif"));
		System.out.println("FileMimeUtils.getFileMimeType(foo.pdf)=" + getFileMimeType("foo.pdf"));
		System.out.println("FileMimeUtils.getFileMimeType(foo.xls)=" + getFileMimeType("foo.xls"));
		System.out.println("FileMimeUtils.getFileMimeType(foo.badextension)=" + getFileMimeType("foo.badextension"));
	}
}
