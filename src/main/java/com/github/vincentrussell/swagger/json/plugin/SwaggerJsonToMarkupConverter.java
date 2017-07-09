package com.github.vincentrussell.swagger.json.plugin;

import io.github.swagger2markup.Swagger2MarkupConverter;
import io.swagger.models.Scheme;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.asciidoctor.AsciiDocDirectoryWalker;
import org.asciidoctor.Asciidoctor;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;
import static org.asciidoctor.Asciidoctor.Factory.create;

public class SwaggerJsonToMarkupConverter implements Closeable {

    private final SwaggerJsonGenerator swaggerJsonGenerator;
    private String basePath = "/";
    private String host = "";
    private String apiInfoTitle = "Api Documentation";
    private String apiInfoDescription = "Api Documentation";
    private String apiInfoVersion = "1.0";
    private String apiInfoTermsOfServiceUrl = "";
    private String apiInfoContactInfoName = "";
    private String apiInfoContactInfoUrl = "";
    private String apiInfoContactInfoEmail = "";
    private String apiInfoLicense = "Apache 2.0";
    private String apiInfoLicenseUrl = "http://www.apache.org/licenses/LICENSE-2.0";
    private Set<Scheme> schemes = new HashSet<>();

    public SwaggerJsonToMarkupConverter(String... contextLocations) {
        swaggerJsonGenerator = new SwaggerJsonGenerator(contextLocations);
    }

    public SwaggerJsonToMarkupConverter(Class... webApplicationContextClasses) {
        swaggerJsonGenerator = new SwaggerJsonGenerator(webApplicationContextClasses);
    }

    public SwaggerJsonToMarkupConverter setBasePath(String basePath) {
        swaggerJsonGenerator.setBasePath(basePath);
        return this;
    }

    public SwaggerJsonToMarkupConverter setHost(String host) {
        swaggerJsonGenerator.setHost(host);
        return this;
    }

    public SwaggerJsonToMarkupConverter setApiInfoTitle(String apiInfoTitle) {
        swaggerJsonGenerator.setBasePath(basePath);
        return this;
    }

    public SwaggerJsonToMarkupConverter setApiInfoDescription(String apiInfoDescription) {
        swaggerJsonGenerator.setApiInfoDescription(apiInfoDescription);
        return this;
    }

    public SwaggerJsonToMarkupConverter setApiInfoVersion(String apiInfoVersion) {
        swaggerJsonGenerator.setApiInfoVersion(apiInfoVersion);
        return this;
    }

    public SwaggerJsonToMarkupConverter setApiInfoTermsOfServiceUrl(String apiInfoTermsOfServiceUrl) {
        swaggerJsonGenerator.setApiInfoTermsOfServiceUrl(apiInfoTermsOfServiceUrl);
        return this;
    }

    public SwaggerJsonToMarkupConverter setApiInfoContactInfoName(String apiInfoContactInfoName) {
        swaggerJsonGenerator.setApiInfoContactInfoName(apiInfoContactInfoName);
        return this;
    }

    public SwaggerJsonToMarkupConverter setApiInfoContactInfoUrl(String apiInfoContactInfoUrl) {
        swaggerJsonGenerator.setApiInfoContactInfoUrl(apiInfoContactInfoUrl);
        return this;
    }

    public SwaggerJsonToMarkupConverter setApiInfoContactInfoEmail(String apiInfoContactInfoEmail) {
        swaggerJsonGenerator.setApiInfoContactInfoEmail(apiInfoContactInfoEmail);
        return this;
    }

    public SwaggerJsonToMarkupConverter setApiInfoLicense(String apiInfoLicense) {
        swaggerJsonGenerator.setApiInfoLicense(apiInfoLicense);
        return this;
    }

    public SwaggerJsonToMarkupConverter setApiInfoLicenseUrl(String apiInfoLicenseUrl) {
        swaggerJsonGenerator.setApiInfoLicenseUrl(apiInfoLicenseUrl);
        return this;
    }

    public SwaggerJsonToMarkupConverter setSchemes(Collection<? extends Object> schemes) {
        swaggerJsonGenerator.setSchemes(schemes);
        return this;
    }

    public SwaggerJsonToMarkupConverter setPathIncludeRegexes(Collection<String> pathRegexes) {
        swaggerJsonGenerator.setPathIncludeRegexes(pathRegexes);
        return this;
    }

    public void writeToDirectory(File directory) throws IOException {
        notNull(directory);
        final String json = swaggerJsonGenerator.getSwaggerJson();
        Swagger2MarkupConverter.from(json)
                .build()
                .toFile(directory.toPath());

        Asciidoctor asciidoctor = create();

        String[] result = asciidoctor.convertDirectory(
                new AsciiDocDirectoryWalker(directory.getAbsolutePath()),
                new HashMap<String, Object>());

        for (String html : result) {
            System.out.println(html);
        }
    }

    @Override
    public void close() throws IOException {
        swaggerJsonGenerator.close();
    }

    public void writeToHtmlFile(File outputFile) throws IOException {
        notNull(outputFile);
        File parentDirectory = outputFile.getParentFile();
        File tempFile = File.createTempFile(FilenameUtils.getBaseName(outputFile.getAbsolutePath()),"", parentDirectory);
        final String json = swaggerJsonGenerator.getSwaggerJson();
        Swagger2MarkupConverter.from(json)
                .build()
                .toFile(tempFile.toPath());

        Asciidoctor asciidoctor = create();
        File tempAsciidocFile = new File(tempFile + ".adoc");
        File tempHtmlfile = new File(tempFile + ".html");
        String html = asciidoctor.convertFile(tempAsciidocFile, new HashMap<String, Object>());
        FileUtils.copyFile(tempHtmlfile, outputFile);
        FileUtils.forceDelete(tempAsciidocFile);
        FileUtils.forceDelete(tempHtmlfile);
        FileUtils.forceDelete(tempFile);
        System.out.println("");
    }
}
