package com.github.vincentrussell.swagger.json.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincentrussell.swagger.json.plugin.applicationConfig.SpringWebConfig;
import com.google.common.collect.ImmutableMap;
import io.swagger.models.Scheme;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class SwaggerJsonGeneratorTest {

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

    @Test
    public void loadContextFromXmlConfigWithoutVariables() throws IOException {
        try (SwaggerJsonGenerator swaggerJsonGenerator = new SwaggerJsonGenerator("classpath:spring-web-servlet.xml")) {
            String json = swaggerJsonGenerator.getSwaggerJson();
            Map<String, Object> jsonObject = parseJson(json);
            assertEquals("/", jsonObject.get("basePath"));
            assertEquals("localhost", jsonObject.get("host"));
            assertThat(((Map)jsonObject.get("paths")).keySet(), (Matcher)hasItems("/hello/", "/hello/hello/{name}",
                    "/goodbye/", "/goodbye/goodbye/{name}"));
        }
    }

    @Test
    public void loadContextFromAnnotationWithoutVariables() throws IOException {
        try (SwaggerJsonGenerator swaggerJsonGenerator = new SwaggerJsonGenerator(SpringWebConfig.class)) {
            String json = swaggerJsonGenerator.getSwaggerJson();
            Map<String, Object> jsonObject = parseJson(json);
            assertEquals("/", jsonObject.get("basePath"));
            assertEquals("localhost", jsonObject.get("host"));
            assertThat(((Map)jsonObject.get("paths")).keySet(), (Matcher)hasItems("/hello/", "/hello/hello/{name}",
                    "/goodbye/", "/goodbye/goodbye/{name}"));
        }

    }

    @Test
    public void loadContextFromXmlConfigWithVariables() throws IOException {
        try (SwaggerJsonGenerator swaggerJsonGenerator = callSetters(new SwaggerJsonGenerator("classpath:spring-web-servlet.xml"))) {
            String json = swaggerJsonGenerator.getSwaggerJson();
            assertVariablesSet(parseJson(json));
        }
    }

    @Test
    public void loadContextFromAnnotationWithVariables() throws IOException {
        try (SwaggerJsonGenerator swaggerJsonGenerator = callSetters(new SwaggerJsonGenerator(SpringWebConfig.class))) {
            String json = swaggerJsonGenerator.getSwaggerJson();
            assertVariablesSet(parseJson(json));
        }

    }

    @Test
    public void loadContextFromXmlConfigWithPathRegexInclude() throws IOException {
        try (SwaggerJsonGenerator swaggerJsonGenerator = new SwaggerJsonGenerator("classpath:spring-web-servlet.xml")
        .setPathIncludeRegexes(Collections.singleton("/hello.+"))) {
            String json = swaggerJsonGenerator.getSwaggerJson();
            Map<String, Object> jsonObject = parseJson(json);
            assertThat(((Map)jsonObject.get("paths")).keySet(), (Matcher)hasItems("/hello/", "/hello/hello/{name}"));
            assertThat(((Map)jsonObject.get("paths")).keySet(), (Matcher)not(hasItems("/goodbye/", "/goodbye/goodbye/{name}")));
        }
    }

    @Test
    public void loadContextFromXmlConfigWithPathRegexExclude() throws IOException {
        try (SwaggerJsonGenerator swaggerJsonGenerator = new SwaggerJsonGenerator("classpath:spring-web-servlet.xml")
                .setPathExcludeRegexes(Collections.singleton("/hello.+"))) {
            String json = swaggerJsonGenerator.getSwaggerJson();
            Map<String, Object> jsonObject = parseJson(json);
            assertThat(((Map)jsonObject.get("paths")).keySet(), (Matcher)hasItems("/goodbye/", "/goodbye/goodbye/{name}"));
            assertThat(((Map)jsonObject.get("paths")).keySet(), (Matcher)not(hasItems("/hello/", "/hello/hello/{name}")));
        }
    }

    private void assertVariablesSet(Map<String, Object> jsonObject) {
        assertNotNull(jsonObject);
        assertEquals(HOST, jsonObject.get("host"));
        assertEquals(BASE_PATH, jsonObject.get("basePath"));
        assertEquals(ImmutableMap.<String,Object>builder()
                .put("description", API_INFO_DESCRIPTION)
                .put("version", API_INFO_VERSION)
                .put("title", API_INFO_TITLE)
                .put("termsOfService", API_INFO_TERMS_OF_SERVICE_URL)
                .put("contact", ImmutableMap.<String,Object>builder()
                        .put("name", API_INFO_CONTACT_INFO_NAME)
                        .put("url", API_INFO_CONTACT_INFO_URL)
                        .put("email", API_INFO_CONTACT_INFO_EMAIL)
                        .build())
                .put("license", ImmutableMap.<String,Object>builder()
                        .put("name", API_INFO_LICENSE)
                        .put("url", API_INFO_LICENSE_URL)
                        .build())
                .build(), jsonObject.get("info"));
    }

    private Map<String, Object> parseJson(String json) throws IOException {
        assertNotNull(json);
        HashMap<String,Object> result =
                new ObjectMapper().readValue(json, HashMap.class);
        return result;
    }

    private SwaggerJsonGenerator callSetters(SwaggerJsonGenerator swaggerJsonGenerator) {
        return swaggerJsonGenerator
                .setBasePath(BASE_PATH)
                .setHost(HOST)
                .setSchemes(SCHEMES)
                .setApiInfoTitle(API_INFO_TITLE)
                .setApiInfoDescription(API_INFO_DESCRIPTION)
                .setApiInfoVersion(API_INFO_VERSION)
                .setApiInfoTermsOfServiceUrl(API_INFO_TERMS_OF_SERVICE_URL)
                .setApiInfoContactInfoName(API_INFO_CONTACT_INFO_NAME)
                .setApiInfoContactInfoEmail(API_INFO_CONTACT_INFO_EMAIL)
                .setApiInfoContactInfoUrl(API_INFO_CONTACT_INFO_URL)
                .setApiInfoContactInfoEmail(API_INFO_CONTACT_INFO_EMAIL)
                .setApiInfoLicense(API_INFO_LICENSE)
                .setApiInfoLicenseUrl(API_INFO_LICENSE_URL);
    }

}
