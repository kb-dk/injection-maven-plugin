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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static dk.kb.util.yaml.YAML.resolveLayeredConfigs;

@Mojo(name = "read-yaml-properties", defaultPhase = LifecyclePhase.VALIDATE)
public class InjectionMavenPlugin extends AbstractMojo{
    @Parameter(defaultValue = "${project}", required  = true, readonly = true)
    public MavenProject project;

    /**
     * The YAML files that will be used when reading properties.
     * To include a YAML file specify the path in a @{code <file></file>} tag inside the <files> tag in the POM.
     */
    @Parameter(property = "yamlResolvers")
    public List<YamlResolver> yamlResolvers;

    public void setYamlResolvers(List<YamlResolver> yamlResolvers) {
        this.yamlResolvers = yamlResolvers;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    /**
     * This MOJO reads specified properties from specified files.
     */
    public void execute() throws MojoExecutionException {
        for (YamlResolver yamlResolver : yamlResolvers) {
            if (yamlResolver.createEnum){
                String collectionEnum = getYamlValueAsEnumFromFile(yamlResolver);

                getLog().info(collectionEnum.toString());
            } else {
                /*// Resolve YAML
                YAML yaml = resolveLayeredConfigs(yamlResolver.filePath);
                // Flatten YAML to entries
                List<Map.Entry<String, Object>> flatYAML = YAMLUtils.flatten(yaml);
                // Filter YAML entries
                List<Map.Entry<String, Object>> nonNullYamlEntries = filterEntries(flatYAML);
                // Add YAML properties to Maven project properties
                addYamlPropertiesToProjectProperties(nonNullYamlEntries);
                getLog().info("Updated Maven project properties with " + nonNullYamlEntries.size() +
                        " properties from: '" + yamlResolver.filePath);*/
                getLog().info("Not ENUM");
            }
        }
    }

    /**
     * Extract values from a list or map of values in a YAML file as an enum, that can be used as a maven property.
     * @param yamlInput a configuration object containing information needed to extract the values from the YAML.
     * @return a string ready to be used as multiple values in an ENUM in OpenAPI.
     *          All values are concatenated by a comma.
     */
    private String getYamlValueAsEnumFromFile(YamlResolver yamlInput) {
        getLog().info("Yaml entry is of type: " + yamlInput.yamlType);

        try {
            switch (yamlInput.yamlType){
                case "List":
                    return constructEnumFromList(yamlInput);

                case "Map":
                    getLog().info("Yaml object is of type map");
                    return "";
                case "Single-value":
                    getLog().info("Yaml object is a single value");
                    return "";
                default:
                    throw new MojoFailureException("Wrong key has been used when configuring the plugin.");
            }

        } catch (MojoFailureException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a string from all entries in a YAML List. The strings are concatenated by ', ' and can be used as an
     * ENUM value in an OpenAPI specification.
     * @param yamlInput a configuration object containing information needed to extract the values from the YAML.
     * @return all values from the given list as a comma seperated string.
     */
    private static String constructEnumFromList(YamlResolver yamlInput) {
        try {
            YAML fullYaml = new YAML(yamlInput.filePath);
            StringJoiner stringJoiner = new StringJoiner(", ");
            List<YAML> propValues = fullYaml.getYAMLList(yamlInput.collectionPath);
            for (YAML originYaml : propValues) {
                String enumEntry = originYaml.getString("[0]." + yamlInput.key);
                stringJoiner.add(enumEntry);
            }
            return stringJoiner.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ========================= OLD CODE =============================
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
    }

}
