package com.github.vincentrussell.swagger.json.plugin;

import com.github.vincentrussell.swagger.json.plugin.applicationConfig.SpringWebConfig;
import io.swagger.models.Scheme;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SwaggerJsonToMarkupConverterTest {

    private static final String BASE_PATH = "/myBasePath/whatever";
    private static final String HOST = "localhost:8080";
    private static final Set<Scheme> SCHEMES = new HashSet<>(Arrays.asList(Scheme.HTTP, Scheme.HTTPS));
    private static final String API_INFO_TITLE = "My title";
    private static final String API_INFO_DESCRIPTION = "My Description";
    private static final String API_INFO_VERSION = "0.0.1";
    private static final String API_INFO_TERMS_OF_SERVICE_URL = "http://termsofserivce.com";
    private static final String API_INFO_CONTACT_INFO_NAME = "John Doe";
    private static final String API_INFO_CONTACT_INFO_URL = "http://www.johndoe.com";
    private static final String API_INFO_CONTACT_INFO_EMAIL = "john@johndoe.com";
    private static final String API_INFO_LICENSE = "Apache 2.0";
    private static final String API_INFO_LICENSE_URL = "http://www.apache.org/licenses/LICENSE-2.0";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File file;


    @Before
    public void before() throws IOException {
        file = temporaryFolder.newFile("output.html");
    }


    @Test
    public void loadContextFromXmlConfig() throws IOException {
        try (SwaggerJsonToMarkupConverter swaggerJsonGenerator = callSetters(new SwaggerJsonToMarkupConverter("classpath:spring-web-servlet.xml"))) {
            swaggerJsonGenerator.writeToHtmlFile(file);
        }
    }

    @Test
    public void loadContextFromAnnotation() throws IOException {
        try (SwaggerJsonToMarkupConverter swaggerJsonGenerator = callSetters(new SwaggerJsonToMarkupConverter(SpringWebConfig.class))) {
            swaggerJsonGenerator.writeToHtmlFile(file);
        }
    }

    private SwaggerJsonToMarkupConverter callSetters(SwaggerJsonToMarkupConverter swaggerJsonToMarkupConverter) {
        return swaggerJsonToMarkupConverter
                .setBasePath(BASE_PATH)
                .setHost(HOST)
                .setSchemes(SCHEMES)
                .setApiInfoTitle(API_INFO_TITLE)
                .setApiInfoDescription(API_INFO_DESCRIPTION)
                .setApiInfoVersion(API_INFO_VERSION)
                .setApiInfoTermsOfServiceUrl(API_INFO_TERMS_OF_SERVICE_URL)
                .setApiInfoContactInfoName(API_INFO_CONTACT_INFO_NAME)
                .setApiInfoContactInfoEmail(API_INFO_CONTACT_INFO_NAME)
                .setApiInfoContactInfoUrl(API_INFO_CONTACT_INFO_URL)
                .setApiInfoContactInfoEmail(API_INFO_CONTACT_INFO_EMAIL)
                .setApiInfoLicense(API_INFO_LICENSE)
                .setApiInfoLicenseUrl(API_INFO_LICENSE_URL);
    }



}