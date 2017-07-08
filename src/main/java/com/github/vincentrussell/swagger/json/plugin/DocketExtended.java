package com.github.vincentrussell.swagger.json.plugin;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.springframework.context.ApplicationContext;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

import static org.springframework.util.StringUtils.isEmpty;

public class DocketExtended extends Docket {

    public static final String SEPARATOR_COMMA = ",";
    public static final String HOST = "host";
    public static final String PROTOCOLS = "protocols";
    public static final String API_INFO_TITLE = "apiInfo.title";
    public static final String API_INFO_DESCRIPTION = "apiInfo.description";
    public static final String API_INFO_VERSION = "apiInfo.version";
    public static final String API_INFO_TERMS_OF_SERVICE_URL = "apiInfo.termsOfServiceUrl";
    public static final String API_INFO_CONTACT_INFO_NAME = "apiInfo.contactInfo.name";
    public static final String API_INFO_CONTACT_INFO_URL = "apiInfo.contactInfo.url";
    public static final String API_INFO_CONTACT_INFO_EMAIL = "apiInfo.contactInfo.email";
    public static final String API_INFO_LICENSE = "apiInfo.license";
    public static final String API_INFO_LICENSE_URL = "apiInfo.licenseUrl";
    public static final String BASE_PATH = "basePath";

    public DocketExtended(DocumentationType documentationType, ApplicationContext applicationContext) {
        super(documentationType);
        host(applicationContext.getEnvironment().getProperty(HOST));
        String protocols = applicationContext.getEnvironment().getProperty(PROTOCOLS);
        if (!isEmpty(protocols)) {
            protocols(Sets.newHashSet(Splitter.on(SEPARATOR_COMMA)
                    .split(protocols)));
        }

        ApiInfo apiInfo = new ApiInfo(applicationContext.getEnvironment().getProperty(API_INFO_TITLE),
                applicationContext.getEnvironment().getProperty(API_INFO_DESCRIPTION),
                applicationContext.getEnvironment().getProperty(API_INFO_VERSION),
                applicationContext.getEnvironment().getProperty(API_INFO_TERMS_OF_SERVICE_URL),
                new Contact(applicationContext.getEnvironment().getProperty(API_INFO_CONTACT_INFO_NAME),
                        applicationContext.getEnvironment().getProperty(API_INFO_CONTACT_INFO_URL),
                        applicationContext.getEnvironment().getProperty(API_INFO_CONTACT_INFO_EMAIL))
                ,applicationContext.getEnvironment().getProperty(API_INFO_LICENSE),
                applicationContext.getEnvironment().getProperty(API_INFO_LICENSE_URL),
                Collections.emptyList());
        apiInfo(apiInfo);

    }
}
