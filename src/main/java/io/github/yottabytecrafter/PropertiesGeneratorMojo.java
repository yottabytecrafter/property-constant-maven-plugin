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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.text.MessageFormat;
import java.util.ArrayList; // For computeIfAbsent in filesByBaseName
import java.util.HashMap;   // For filesByBaseName and propertyTranslations
import java.util.List;
import java.util.Locale;
import java.util.Map;     // For filesByBaseName and propertyTranslations
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher; // For pattern matching file names
import java.util.regex.Pattern;   // For compiling regex for file names
import java.util.stream.Collectors; // For collecting file names for logging

/**
 * Maven plugin to generate Java constant classes from property files.
 * This plugin scans .properties files and creates corresponding Java classes with static string constants.
 */
@Mojo(name = "generate-properties")
public class PropertiesGeneratorMojo extends AbstractMojo {

    private ResourceBundle messages;

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

    @Parameter(property = "property-constant.propertiesEncoding", defaultValue = "UTF-8")
    private String propertiesEncoding = "UTF-8"; // Initialize with default

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public void execute() throws MojoExecutionException {
        try {
            messages = ResourceBundle.getBundle("io.github.yottabytecrafter.messages", Locale.getDefault());
        } catch (Exception e) {
            getLog().error("Could not load resource bundle: " + e.getMessage());
            throw new MojoExecutionException("Could not load resource bundle", e);
        }

        getLog().info(messages.getString("mojo.greeting"));

        try {
            ClassNameStrategy strategy = ClassNameStrategyFactory.createStrategy(classNameStrategyClass);

            if (sources == null || sources.isEmpty()) {
                getLog().warn(messages.getString("mojo.noSourcesConfigured"));
                return;
            }

            for (Source source : sources) {
                getLog().info(MessageFormat.format(messages.getString("mojo.processingSource"), source.getPath()));
                processSource(source, strategy);
            }

        } catch (Exception e) {
            throw new MojoExecutionException(messages.getString("mojo.errorGeneratingConstants"), e);
        }
    }

    private void processSource(Source source, ClassNameStrategy strategy) throws IOException, MojoExecutionException {
        File sourcePath = new File(source.getPath());

        if (!sourcePath.exists()) {
            getLog().warn(MessageFormat.format(messages.getString("mojo.sourcePathDoesNotExist"), sourcePath));
            return;
        }

        getLog().info(MessageFormat.format(messages.getString("mojo.resolvedSourcePath"), sourcePath.getAbsolutePath()));
        getLog().info(MessageFormat.format(messages.getString("mojo.sourceExists"), sourcePath.exists()));
        getLog().info(MessageFormat.format(messages.getString("mojo.sourceIsDirectory"), sourcePath.isDirectory()));

        if (sourcePath.isFile()) {
            // Log a warning as the plugin now primarily expects a directory for i18n properties
            getLog().warn(MessageFormat.format(messages.getString("mojo.sourcePathIsFile"), sourcePath.getAbsolutePath()));
            // Optionally, could attempt to process it if it matches the new naming convention,
            // but the primary design is for directories. For now, just warn and return.
            return;
        } else if (sourcePath.isDirectory()) {
            Map<String, List<File>> filesByBaseName = new HashMap<>();
            File[] allFiles = sourcePath.listFiles();
            if (allFiles == null) {
                return; // Or log a warning if directory is unreadable
            }

            Pattern pattern = Pattern.compile("(.*)_([a-z]{2}(?:_[A-Z]{2})?)\\.properties");

            for (File file : allFiles) {
                if (file.isFile()) {
                    Matcher matcher = pattern.matcher(file.getName());
                    if (matcher.matches()) {
                        String baseName = matcher.group(1);
                        filesByBaseName.computeIfAbsent(baseName, k -> new ArrayList<>()).add(file);
                    }
                }
            }

            for (Map.Entry<String, List<File>> entry : filesByBaseName.entrySet()) {
                String baseName = entry.getKey();
                List<File> languageFiles = entry.getValue();
                getLog().info(MessageFormat.format(messages.getString("mojo.processingPropertyGroup"),
                        baseName, languageFiles.stream().map(File::getName).collect(Collectors.toList())));
                processPropertyGroup(baseName, languageFiles, source.getTargetPackage(), strategy);
            }
        }
    }

    private void processPropertyGroup(String baseName, List<File> languageFiles, String targetPackage,
                                      ClassNameStrategy strategy) throws IOException, MojoExecutionException {
        Map<String, Map<String, String>> propertyTranslations = new HashMap<>();
        Charset charset;
        try {
            charset = Charset.forName(propertiesEncoding);
        } catch (UnsupportedCharsetException | java.nio.charset.IllegalCharsetNameException e) {
            getLog().warn(MessageFormat.format(messages.getString("mojo.unsupportedEncoding"), propertiesEncoding), e);
            charset = StandardCharsets.UTF_8;
        }

        Pattern filePattern = Pattern.compile(".*_([a-z]{2}(?:_[A-Z]{2})?)\\.properties");

        for (File langFile : languageFiles) {
            Matcher matcher = filePattern.matcher(langFile.getName());
            if (!matcher.matches()) {
                getLog().warn(MessageFormat.format(messages.getString("mojo.invalidLanguageFile"), langFile.getName()));
                continue;
            }
            String langCode = matcher.group(1);
            getLog().info(MessageFormat.format(messages.getString("mojo.loadingPropertiesForLang"), langCode, langFile.getName()));

            Properties properties = new Properties();
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(langFile), charset)) {
                properties.load(reader);
            } catch (FileNotFoundException e) {
                getLog().error(MessageFormat.format(messages.getString("mojo.propertiesFileNotFound"), langFile.getAbsolutePath()), e);
                // Optionally continue to process other files or rethrow
                continue;
            } catch (IOException e) {
                getLog().error(MessageFormat.format(messages.getString("mojo.errorReadingPropertiesFile"), langFile.getAbsolutePath()), e);
                // Optionally continue to process other files or rethrow
                continue;
            }

            for (String key : properties.stringPropertyNames()) {
                propertyTranslations.computeIfAbsent(key, k -> new HashMap<>()).put(langCode, properties.getProperty(key));
            }
        }

        if (propertyTranslations.isEmpty()) {
            getLog().warn("No properties found for basename: " + baseName);
            return;
        }

        String pluginVersion = pluginDescriptor == null ? "unknown" : pluginDescriptor.getVersion();

        PropertiesClassGenerator generator = new PropertiesClassGenerator(
                targetPackage,
                outputDirectory,
                strategy, // This strategy should now use baseName
                pluginVersion
        );
        // The generateClass method signature has been changed:
        generator.generateClass(baseName, propertyTranslations);
        getLog().info(MessageFormat.format(messages.getString("mojo.generatedFileForGroup"),
                                           strategy.generateClassName(baseName), targetPackage));
    }
}
