package dk.kb.util;

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

import dk.kb.util.yaml.YAML;
import dk.kb.util.yaml.YAMLUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import static dk.kb.util.yaml.YAML.resolveLayeredConfigs;

@Mojo(name = "read-yaml-properties", defaultPhase = LifecyclePhase.VALIDATE)
public class InjectionMavenPlugin extends AbstractMojo{
    @Parameter(defaultValue = "${project}", required  = true, readonly = true)
    public MavenProject project;

    /**
     * The YAML files that will be used when reading properties.
     * To include a YAML file specify the path in a @{code <file></file>} tag.
     */
    @Parameter(property = "files", required = true)
    public List<String> files;

    /**
     * This MOJO reads properties from @{code files}
     */
    public void execute() throws MojoExecutionException {
        // Retrieve the properties from each YAML file
        for (String yamlPath : files) {
            try {
                // Resolve YAML
                YAML yaml = resolveLayeredConfigs(yamlPath);
                // Flatten YAML to entries
                List<Map.Entry<String, Object>> flatYAML = YAMLUtils.flatten(yaml);
                // Filter YAML entries
                List<Map.Entry<String, Object>> nonNullYamlEntries = filterEntries(flatYAML);
                // Add YAML properties to Maven project properties
                addYamlPropertiesToProjectProperties(nonNullYamlEntries);
                getLog().info("Updated Maven project properties with properties from: '" + yamlPath);
            } catch (IOException e) {
                throw new MojoExecutionException("The MOJO could not be executed. YAML input file could probably not be loaded.");
            }
        }
    }

    /**
     * Filters a list of parameters from a flattened YAML file.
     * @param flatYAML entries from a YAML file that have already been flattened.
     * @return a list of key/value pairs containing properties that are not null or
     * 			doesn't have keys or values equal null.
     */
    private static List<Map.Entry<String, Object>> filterEntries(List<Map.Entry<String, Object>> flatYAML) {
        return flatYAML.stream()
                .filter(Objects::nonNull)
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toList());
    }

    /**
     * Add the entries from the input list to the Maven project properties.
     * @param nonNullYamlEntries a list of properties which are to be added to maven properties.
     */
    private void addYamlPropertiesToProjectProperties(List<Map.Entry<String, Object>> nonNullYamlEntries) {
        Properties yamlProperties = new Properties();
        nonNullYamlEntries.forEach(entry -> addEntryToProperties(entry, yamlProperties));
        project.getProperties().putAll(yamlProperties);
    }

    private void addEntryToProperties(Map.Entry<String, Object> entry, Properties yamlProperties) {
        yamlProperties.put(entry.getKey(), entry.getValue());
        getLog().info("Added the property: '" + entry.getKey() + "'.");
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }
}
