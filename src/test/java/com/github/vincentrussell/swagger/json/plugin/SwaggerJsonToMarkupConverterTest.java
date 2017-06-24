package com.github.vincentrussell.swagger.json.plugin;

import com.github.vincentrussell.swagger.json.plugin.applicationConfig.SpringWebConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class SwaggerJsonToMarkupConverterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File folder;


    @Before
    public void before() throws IOException {
        folder = temporaryFolder.newFolder();
    }


    @Test
    public void loadContextFromXmlConfig() throws IOException {
        try (SwaggerJsonToMarkupConverter swaggerJsonGenerator = new SwaggerJsonToMarkupConverter("classpath:spring-web-servlet.xml")) {
            swaggerJsonGenerator.writeToDirectory(folder);
        }
    }

    @Test
    public void loadContextFromAnnotation() throws IOException {
        try (SwaggerJsonToMarkupConverter swaggerJsonGenerator = new SwaggerJsonToMarkupConverter(SpringWebConfig.class)) {
            swaggerJsonGenerator.writeToDirectory(folder);
        }
    }



}