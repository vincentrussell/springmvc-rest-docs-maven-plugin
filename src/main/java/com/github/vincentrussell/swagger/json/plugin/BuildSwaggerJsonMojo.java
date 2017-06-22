package com.github.vincentrussell.swagger.json.plugin;

import org.apache.commons.io.IOUtils;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.apache.commons.lang3.Validate.isTrue;

@Mojo( name = "build-swagger-json", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, requiresDependencyResolution = ResolutionScope.RUNTIME )
public class BuildSwaggerJsonMojo extends AbstractMojo  {

    @Parameter(defaultValue = "${project.build.directory}/swagger-json/swagger.json", property = "destinationFile", required = true)
    protected String destinationFile;

    @Parameter(property = "xmlConfigs", required = false)
    protected String[] xmlConfigs = new String[0];

    @Parameter(property = "annotatedClassConfigs", required = false)
    protected String[] annotatedClassConfigs = new String[0];

    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        isTrue((xmlConfigs!=null && xmlConfigs.length > 0)
                ||  (annotatedClassConfigs!=null && annotatedClassConfigs.length > 0), "xmlConfigs or annotatedClassConfigs must be provided");


      setProjectRuntimeDependenciesOnPluginClasspath();

        File f = new File(destinationFile);
        if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            if (isXmlMode()) {
                try (SwaggerJsonGenerator swaggerJsonGenerator =
                             new SwaggerJsonGenerator(xmlConfigs)) {
                    IOUtils.write(swaggerJsonGenerator.getSwaggerJson(), fileOutputStream);
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
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
