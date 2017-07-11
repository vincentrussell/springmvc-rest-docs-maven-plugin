package com.github.vincentrussell.restdocs.maven.plugin;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.apache.commons.lang3.Validate.isTrue;

@Mojo( name = "build-api-docs-html", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, requiresDependencyResolution = ResolutionScope.RUNTIME )
public class BuildApiDocsMojo extends AbstractMojo  {

    @Parameter(defaultValue = "${project.build.directory}/html-api-docs/api-docs.html", property = "destinationFile", required = true)
    protected String destinationFile;

    @Parameter(property = "xmlConfigs", required = false)
    protected String[] xmlConfigs = new String[0];

    @Parameter(property = "annotatedClassConfigs", required = false)
    protected String[] annotatedClassConfigs = new String[0];

    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject mavenProject;


    @Parameter(property = "basePath", required = false)
    private String basePath = "/";
    @Parameter(property = "host", required = false)
    private String host = "";
    @Parameter(property = "title", required = false)
    private String title = "Api Documentation";
    @Parameter(property = "description", required = false)
    private String description = "Api Documentation";
    @Parameter(property = "version", defaultValue = "${project.version}", required = false)
    private String version = "1.0";
    @Parameter(property = "termsOfServiceUrl", required = false)
    private String termsOfServiceUrl = "";
    @Parameter(property = "contactName", required = false)
    private String contactName = "";
    @Parameter(property = "contactUrl", required = false)
    private String contactUrl = "";
    @Parameter(property = "contactEmail", required = false)
    private String contactEmail = "";
    @Parameter(property = "license", required = false)
    private String license = "Apache 2.0";
    @Parameter(property = "licenseUrl", required = false)
    private String licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0";
    @Parameter(property = "schemes", required = false)
    private String[] schemes = new String[0];
    @Parameter(property = "pathIncludeRegexes", required = false)
    private String[] pathIncludeRegexes = new String[0];
    @Parameter(property = "pathExcludeRegexes", required = false)
    private String[] pathExcludeRegexes = new String[0];

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        isTrue((xmlConfigs!=null && xmlConfigs.length > 0)
                ||  (annotatedClassConfigs!=null && annotatedClassConfigs.length > 0), "xmlConfigs or annotatedClassConfigs must be provided");


      setProjectRuntimeDependenciesOnPluginClasspath();

        final File destFile = new File(destinationFile);
        if (destFile.getParentFile() != null) {
            destFile.getParentFile().mkdirs();
        }

            try {

                try (SwaggerJsonToMarkupConverter swaggerJsonToMarkupConverter =
                             isXmlMode() ? new SwaggerJsonToMarkupConverter(xmlConfigs) :
                new SwaggerJsonToMarkupConverter(Lists.transform(Lists.newArrayList(annotatedClassConfigs), new Function<String, Class>() {
                    @Override
                    public Class apply(String input) {
                        try {
                            return Class.forName(input);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }
                }).toArray(new Class[annotatedClassConfigs.length]))) {

                    swaggerJsonToMarkupConverter.setBasePath(basePath)
                            .setHost(host)
                            .setApiInfoTitle(title)
                            .setApiInfoDescription(description)
                            .setApiInfoVersion(version)
                            .setApiInfoTermsOfServiceUrl(termsOfServiceUrl)
                            .setApiInfoContactInfoName(contactName)
                            .setApiInfoContactInfoUrl(contactUrl)
                            .setApiInfoContactInfoEmail(contactEmail)
                            .setApiInfoLicense(license)
                            .setApiInfoLicenseUrl(licenseUrl);

                    swaggerJsonToMarkupConverter.writeToHtmlFile(destFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    private void setProjectRuntimeDependenciesOnPluginClasspath() throws MojoExecutionException {
        try {
            List<String> runtimeClasspathElements = runtimeClasspathElements = mavenProject.getRuntimeClasspathElements();
            URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
            for (int i = 0; i < runtimeClasspathElements.size(); i++) {
                String element = runtimeClasspathElements.get(i);
                runtimeUrls[i] = new File(element).toURI().toURL();
            }
            URLClassLoader newLoader = new URLClassLoader(runtimeUrls,
                    Thread.currentThread().getContextClassLoader());

            Thread.currentThread().setContextClassLoader(newLoader);
            getLog().info("Plugin classpath augmented with project compile and runtime dependencies: " + runtimeClasspathElements);
        } catch (DependencyResolutionRequiredException | MalformedURLException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }

    private boolean isXmlMode() {
        return (xmlConfigs!=null && xmlConfigs.length > 0);
    }

}
