package com.github.vincentrussell.swagger.json.plugin;

import io.github.swagger2markup.Swagger2MarkupConverter;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import static org.apache.commons.lang3.Validate.notNull;

public class SwaggerJsonToMarkupConverter implements Closeable {

    private final SwaggerJsonGenerator swaggerJsonGenerator;

    public SwaggerJsonToMarkupConverter(String... contextLocations) {
        swaggerJsonGenerator = new SwaggerJsonGenerator(contextLocations);
    }

    public SwaggerJsonToMarkupConverter(Class... webApplicationContextClasses) {
        swaggerJsonGenerator = new SwaggerJsonGenerator(webApplicationContextClasses);
    }

    public void writeToDirectory(File directory) throws IOException {
        notNull(directory);
        final String json = swaggerJsonGenerator.getSwaggerJson();
        Swagger2MarkupConverter.from(json)
                .build()
                .toFolder(directory.toPath());
    }

    @Override
    public void close() throws IOException {
        swaggerJsonGenerator.close();
    }
}
