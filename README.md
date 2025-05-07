# validation-mimetypes-spring-boot-starter


### 说明

 > 基于validation-api + mimetypes 实现的文件类型校验

### Maven

``` xml
<dependency>
	<groupId>com.github.hiwepy</groupId>
	<artifactId>validation-mimetypes-spring-boot-starter</artifactId>
	<version>${project.version}</version>
</dependency>
```

> 默认使用非严格模式， 即允许文件扩展名和MIME类型不匹配的情况。如果需要严格模式，请在添加一下依赖：

``` yaml
<!-- Tika文件解析器整合包 -->
<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-parsers-standard-package</artifactId>
    <version>${tika.version}</version>
</dependency>
```

并指定 `@FileNotEmpty` 注解的 `strict` 属性为 `true`，如下所示：

``` java
@FileNotEmpty(strict = true, extensions = { "jpg", "png" }, mimeTypes = { "image/jpeg", "image/png" })
private MultipartFile file;
```
