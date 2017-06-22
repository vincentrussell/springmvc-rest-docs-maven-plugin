package com.github.vincentrussell.swagger.json.plugin;

import com.google.common.collect.Lists;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class SwaggerJsonGenerator implements Closeable {

    public static final String SWAGGER_REST_ENDPOINT = "/v2/api-docs";
    private final WebApplicationContext webApplicationContext;
    private final MockServletContext servletContext = new MockServletContext();
    private final MockHttpSession session = new MockHttpSession();
    private final MockMvc mockMvc;

    public SwaggerJsonGenerator(String... contextLocations) {
        this(getContextFromString(contextLocations));
    }

    public SwaggerJsonGenerator(Class... webApplicationContextClasses) {
        this(getContextFromClass(webApplicationContextClasses));
    }

    private static WebApplicationContext getContextFromClass(Class... webApplicationContextClasses) {
        AnnotationConfigWebApplicationContext annotationConfigWebApplicationContext = new AnnotationConfigWebApplicationContext();
        List<Class> configClasses = Lists.newArrayList(webApplicationContextClasses);
        configClasses.add(Swagger2DocumentationConfiguration.class);
        annotationConfigWebApplicationContext.register(configClasses.toArray(new Class[configClasses.size()]));
        return annotationConfigWebApplicationContext;
    }

    private static WebApplicationContext getContextFromString(String... contextLocations) {
            XmlWebApplicationContext context = new XmlWebApplicationContext();
            List<String> contextLocationList = Lists.newArrayList(contextLocations);
            contextLocationList.add("classpath:swagger-context.xml");
            context.setConfigLocations(contextLocationList.toArray(new String[contextLocations.length]));
            return context;
    }

    public SwaggerJsonGenerator(WebApplicationContext applicationContext) {
        this.webApplicationContext = applicationContext;
        ConfigurableWebApplicationContext webappContext = (ConfigurableWebApplicationContext)webApplicationContext;
        webappContext.setServletContext(servletContext);
        webappContext.refresh();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webappContext).build();
    }

    String getSwaggerJson() throws IOException {
        String result = null;
        try {
            result = this.mockMvc.perform(get(SWAGGER_REST_ENDPOINT).session(session)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            throw new IOException(e);
        }

        return result;
    }

    @PreDestroy
    public void close() throws IOException {
        ((ConfigurableApplicationContext)webApplicationContext).close();
    }
}
