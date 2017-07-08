package com.github.vincentrussell.swagger.json.plugin;

import io.github.swagger2markup.Swagger2MarkupConverter;
import org.asciidoctor.AsciiDocDirectoryWalker;
import org.asciidoctor.Asciidoctor;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.apache.commons.lang3.Validate.notNull;
import static org.asciidoctor.Asciidoctor.Factory.create;

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
}
