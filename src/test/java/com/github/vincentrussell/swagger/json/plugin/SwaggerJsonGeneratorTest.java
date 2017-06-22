package com.github.vincentrussell.swagger.json.plugin;

import com.github.vincentrussell.swagger.json.plugin.applicationConfig.SpringWebConfig;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;

public class SwaggerJsonGeneratorTest {

    @Test
    public void loadContextFromXmlConfig() throws IOException {
        try (SwaggerJsonGenerator swaggerJsonGenerator = new SwaggerJsonGenerator("classpath:spring-web-servlet.xml")) {
            String json = swaggerJsonGenerator.getSwaggerJson();
            assertNotNull(json);
        }
    }

    @Test
    public void loadContextFromAnnotation() throws IOException {
        try (SwaggerJsonGenerator swaggerJsonGenerator = new SwaggerJsonGenerator(SpringWebConfig.class)) {
            String json = swaggerJsonGenerator.getSwaggerJson();
            assertNotNull(json);
        }
    }

}
