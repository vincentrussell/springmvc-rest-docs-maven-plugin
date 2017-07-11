package com.github.vincentrussell.restdocs.maven.plugin;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.springframework.context.ApplicationContext;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

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
    public static final String PATH_INCLUDE_REGEXES = "pathIncludeRegexes";
    public static final String PATH_EXCLUDE_REGEXES = "pathExcludeRegexes";

    public DocketExtended(DocumentationType documentationType, ApplicationContext applicationContext) {
        super(documentationType);
        host(applicationContext.getEnvironment().getProperty(HOST));
        String protocols = applicationContext.getEnvironment().getProperty(PROTOCOLS);
        if (!isEmpty(protocols)) {
            protocols(Sets.newHashSet(Splitter.on(SEPARATOR_COMMA)
                    .split(protocols)));
        }

        String pathIncludesRegexes = applicationContext.getEnvironment().getProperty(PATH_INCLUDE_REGEXES);
        String pathExcludeRegexes = applicationContext.getEnvironment().getProperty(PATH_EXCLUDE_REGEXES);
        if (!isEmpty(pathIncludesRegexes) || !isEmpty(pathExcludeRegexes)) {
            select()
            .apis(RequestHandlerSelectors.any())
            .paths(new RegexPredicate(pathIncludesRegexes, pathExcludeRegexes))
            .build();
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

    private static class RegexPredicate implements Predicate<String> {

        private final Set<Pattern> pathIncludeRegexes;
        private final Set<Pattern> pathExcludeRegexes;

        private RegexPredicate(String pathIncludeRegexes, String pathExcludeRegexes) {
            this.pathIncludeRegexes = !isEmpty(pathIncludeRegexes) ? Sets.newHashSet(Iterables.transform(Splitter.on(SEPARATOR_COMMA)
                    .split(pathIncludeRegexes), input -> Pattern.compile(input))) : Collections.emptySet();
            this.pathExcludeRegexes = !isEmpty(pathExcludeRegexes) ? Sets.newHashSet(Iterables.transform(Splitter.on(SEPARATOR_COMMA)
                    .split(pathExcludeRegexes), input -> Pattern.compile(input))) : Collections.emptySet();
        }

        @Override
        public boolean apply(String input) {
            for (Pattern pattern : pathIncludeRegexes) {
                if (pattern.matcher(input).matches()) {
                    return true;
                }
            }
            for (Pattern pattern : pathExcludeRegexes) {
                if (pattern.matcher(input).matches()) {
                    return false;
                }
            }
            return pathIncludeRegexes.size() == 0 ? true : false;
        }
    }
}
