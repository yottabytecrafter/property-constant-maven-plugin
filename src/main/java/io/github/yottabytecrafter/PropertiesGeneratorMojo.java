package io.github.yottabytecrafter;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import io.github.yottabytecrafter.generator.PropertiesClassGenerator;
import io.github.yottabytecrafter.source.Source;
import io.github.yottabytecrafter.strategy.ClassNameStrategy;
import io.github.yottabytecrafter.factory.ClassNameStrategyFactory;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * Maven plugin to generate Java constant classes from property files.
 * This plugin scans .properties files and creates corresponding Java classes with static string constants.
 */
@Mojo(name = "generate-properties")
public class PropertiesGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor pluginDescriptor;

    /**
     * List of source configurations.
     */
    @Parameter
    private List<Source> sources;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/java")
    private File outputDirectory;

    @Parameter
    private String classNameStrategyClass;

    public void execute() throws MojoExecutionException {
        try {
            ClassNameStrategy strategy = ClassNameStrategyFactory.createStrategy(classNameStrategyClass);

            if (sources == null || sources.isEmpty()) {
                getLog().warn("No sources configured");
                return;
            }

            for (Source source : sources) {
                getLog().info("Processing source with path: " + source.getPath());
                processSource(source, strategy);
            }

        } catch (Exception e) {
            throw new MojoExecutionException("Error generating constants", e);
        }
    }

    private void processSource(Source source, ClassNameStrategy strategy) throws IOException {
        File sourcePath = new File(source.getPath());

        if (!sourcePath.exists()) {
            getLog().warn("Source path does not exist: " + sourcePath);
            return;
        }

        getLog().info("Resolved sourcePath: " + sourcePath.getAbsolutePath());
        getLog().info("Source exists: " + sourcePath.exists());
        getLog().info("Source is directory: " + sourcePath.isDirectory());

        if (sourcePath.isFile()) {
            if (sourcePath.getName().endsWith(".properties")) {
                processPropertiesFile(sourcePath, source.getTargetPackage(), strategy);
            }
        } else if (sourcePath.isDirectory()) {
            File[] propertiesFiles = sourcePath.listFiles((dir, name) -> name.endsWith(".properties"));
            if (propertiesFiles != null) {
                for (File propertiesFile : propertiesFiles) {
                    processPropertiesFile(propertiesFile, source.getTargetPackage(), strategy);
                }
            }
        }
    }

    private void processPropertiesFile(File propertiesFile, String targetPackage,
                                       ClassNameStrategy strategy) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            properties.load(fis);
        }

        String pluginVersion = pluginDescriptor == null ? "unknown" : pluginDescriptor.getVersion();

        PropertiesClassGenerator generator = new PropertiesClassGenerator(
                targetPackage,
                outputDirectory,
                strategy,
                pluginVersion
        );
        generator.generateClass(propertiesFile, properties);
        getLog().info("Generated constants file for: " + propertiesFile.getName() +
                " in package: " + targetPackage);
    }
}
