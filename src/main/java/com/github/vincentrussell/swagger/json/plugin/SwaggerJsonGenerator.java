package com.github.vincentrussell.swagger.json.plugin;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.swagger.models.Scheme;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class SwaggerJsonGenerator implements Closeable {

    public static final String SWAGGER_REST_ENDPOINT = "/v2/api-docs";
    private final WebApplicationContext webApplicationContext;
    private final MockServletContext servletContext = new MockServletContext();
    private final MockHttpSession session = new MockHttpSession();
    private MockMvc mockMvc;
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

    public SwaggerJsonGenerator(String... contextLocations) {
        this(getContextFromString(contextLocations));
    }

    public SwaggerJsonGenerator(Class... webApplicationContextClasses) {
        this(getContextFromClass(webApplicationContextClasses));
    }

    public SwaggerJsonGenerator setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public SwaggerJsonGenerator setHost(String host) {
        this.host = host;
        return this;
    }

    public SwaggerJsonGenerator setSchemes(Collection<? extends Object> schemes) {
        this.schemes = Sets.newHashSet(Iterables.transform(schemes, object -> {
            if (String.class.isInstance(object)) {
                return Scheme.valueOf((String)object);
            } else if (Scheme.class.isInstance(object)) {
                return (Scheme) object;
            } else {
                throw new IllegalArgumentException("can not understand type: " + object.getClass());
            }
        }));
        return this;
    }


    public SwaggerJsonGenerator setApiInfoTitle(String apiInfoTitle) {
        this.apiInfoTitle = apiInfoTitle;
        return this;
    }

    public SwaggerJsonGenerator setApiInfoDescription(String apiInfoDescription) {
        this.apiInfoDescription = apiInfoDescription;
        return this;
    }

    public SwaggerJsonGenerator setApiInfoVersion(String apiInfoVersion) {
        this.apiInfoVersion = apiInfoVersion;
        return this;
    }

    public SwaggerJsonGenerator setApiInfoTermsOfServiceUrl(String apiInfoTermsOfServiceUrl) {
        this.apiInfoTermsOfServiceUrl = apiInfoTermsOfServiceUrl;
        return this;
    }

    public SwaggerJsonGenerator setApiInfoContactInfoName(String apiInfoContactInfoName) {
        this.apiInfoContactInfoName = apiInfoContactInfoName;
        return this;
    }

    public SwaggerJsonGenerator setApiInfoContactInfoUrl(String apiInfoContactInfoUrl) {
        this.apiInfoContactInfoUrl = apiInfoContactInfoUrl;
        return this;
    }

    public SwaggerJsonGenerator setApiInfoContactInfoEmail(String apiInfoContactInfoEmail) {
        this.apiInfoContactInfoEmail = apiInfoContactInfoEmail;
        return this;
    }

    public SwaggerJsonGenerator setApiInfoLicense(String apiInfoLicense) {
        this.apiInfoLicense = apiInfoLicense;
        return this;
    }

    public SwaggerJsonGenerator setApiInfoLicenseUrl(String apiInfoLicenseUrl) {
        this.apiInfoLicenseUrl = apiInfoLicenseUrl;
        return this;
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
    }

    String getSwaggerJson() throws IOException {
        init();
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

    private void init() {
        servletContext.setContextPath(basePath);
        ConfigurableWebApplicationContext webappContext = (ConfigurableWebApplicationContext)webApplicationContext;
        webappContext.getEnvironment().getPropertySources().addLast(
                new CustomPropertySource(this)
        );
        webappContext.setServletContext(servletContext);
        webappContext.refresh();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webappContext).build();
    }

    @PreDestroy
    public void close() throws IOException {
        ((ConfigurableApplicationContext)webApplicationContext).close();
    }

    private static class CustomPropertySource extends PropertySource<String> {

        private final SwaggerJsonGenerator swaggerJsonGenerator;

        public CustomPropertySource(SwaggerJsonGenerator swaggerJsonGenerator) {super("custom");
            this.swaggerJsonGenerator = swaggerJsonGenerator;
        }

        @Override
        public String getProperty(String name) {
            if (DocketExtended.HOST.equals(name)) {
                return swaggerJsonGenerator.host;
            } else if (DocketExtended.BASE_PATH.equals(name)) {
                return swaggerJsonGenerator.basePath;
            } else if (DocketExtended.PROTOCOLS.equals(name)) {
                return Joiner.on(DocketExtended.SEPARATOR_COMMA).join(swaggerJsonGenerator.schemes);
            } else if (DocketExtended.API_INFO_TITLE.equals(name)) {
                return swaggerJsonGenerator.apiInfoTitle;
            } else if (DocketExtended.API_INFO_DESCRIPTION.equals(name)) {
                return swaggerJsonGenerator.apiInfoDescription;
            } else if (DocketExtended.API_INFO_VERSION.equals(name)) {
                return swaggerJsonGenerator.apiInfoVersion;
            } else if (DocketExtended.API_INFO_TERMS_OF_SERVICE_URL.equals(name)) {
                return swaggerJsonGenerator.apiInfoTermsOfServiceUrl;
            } else if (DocketExtended.API_INFO_CONTACT_INFO_NAME.equals(name)) {
                return swaggerJsonGenerator.apiInfoContactInfoName;
            } else if (DocketExtended.API_INFO_CONTACT_INFO_URL.equals(name)) {
                return swaggerJsonGenerator.apiInfoContactInfoUrl;
            } else if (DocketExtended.API_INFO_CONTACT_INFO_EMAIL.equals(name)) {
                return swaggerJsonGenerator.apiInfoContactInfoEmail;
            } else if (DocketExtended.API_INFO_LICENSE.equals(name)) {
                return swaggerJsonGenerator.apiInfoLicense;
            } else if (DocketExtended.API_INFO_LICENSE_URL.equals(name)) {
                return swaggerJsonGenerator.apiInfoLicenseUrl;
            }
            return null;
        }
    }
}
