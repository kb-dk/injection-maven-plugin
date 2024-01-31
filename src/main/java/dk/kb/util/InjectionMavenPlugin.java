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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;


@Mojo(name = "read-yaml-properties", defaultPhase = LifecyclePhase.VALIDATE)
public class InjectionMavenPlugin extends AbstractMojo{
    @Parameter(defaultValue = "${project}", required  = true, readonly = true)
    public MavenProject project;

    /**
     * The path to the input YAML files. Wildcards can be used here. Specifying a path as e.g. myproject-*.yaml will
     * resolve to multiple files, which are then merged by the underlying YAML-implementation. This is useful, when
     * different environments specify different values for the same property.
     */
    @Parameter(property = "filePath")
    public String filePath;

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

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * This MOJO reads specified properties from specified files.
     */
    public void execute() throws MojoExecutionException {
        for (YamlResolver yamlResolver : yamlResolvers) {
            String propertyName;

            if (yamlResolver.createEnum){
                propertyName = yamlResolver.yamlPath;
                String collectionEnum = getYamlValueAsEnumFromFile(yamlResolver);

                addYamlPropertiesToProjectProperties(propertyName, collectionEnum);
            } else {
                propertyName = yamlResolver.yamlPath;
                String value =  getSingleValue(yamlResolver);

                addYamlPropertiesToProjectProperties(propertyName, value);
            }
        }
    }

    /**
     * Extract values from a list, sequence or map of values in a YAML file as an enum, that can be used as a maven property.
     * @param yamlInput a configuration object containing information needed to extract the values from the YAML.
     * @return a string ready to be used as multiple values in an ENUM in OpenAPI.
     *          All values are concatenated by a comma. If a single value path has been given as input, then the single
     *          value is retrieved.
     */
    private String getYamlValueAsEnumFromFile(YamlResolver yamlInput) {
        try {
            switch (yamlInput.yamlType){
                case "Sequence":
                    return createEnumFromYamlValues(yamlInput);
                case "List":
                    return constructEnumFromLists(yamlInput);
                case "Map":
                    return constructEnumFromMap(yamlInput);
                case "Single-value":
                    return getSingleValue(yamlInput);
                default:
                    throw new MojoFailureException("Wrong key: '" + yamlInput.yamlType + "' has been used when configuring the plugin. " +
                            "Valid keys are: 'Sequence', 'List', 'Map' and 'Single-value'.");
            }

        } catch (MojoFailureException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a string from all entries in a YAML map. The strings are concatenated by ', ' and
     * can be used as an ENUM value in an OpenAPI specification.
     * @param yamlInput a configuration object containing information needed to extract the values from the YAML.
     * @return all values from the given map as a comma separated string.
     */
    private String constructEnumFromMap(YamlResolver yamlInput) {
        try {
            YAML fullYaml = YAML.resolveLayeredConfigs(filePath);
            StringJoiner stringJoiner = new StringJoiner(", ");
            YAML propertiesMap = fullYaml.getSubMap(yamlInput.yamlPath);
            propertiesMap.forEach((key, value) -> stringJoiner.add(value.toString()));
            return stringJoiner.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a string from all entries in a YAML List. The strings are concatenated by ', ' and
     * can be used as an ENUM value in an OpenAPI specification. It is possible to extract subelements from complex
     * list by defining a key value in the plugin configuration.
     * @param yamlInput a configuration object containing information needed to extract the values from the YAML.
     * @return all values from the given list as a comma separated string.
     */
    private String constructEnumFromLists(YamlResolver yamlInput){
        if (yamlInput.key == null || yamlInput.key.isEmpty()){
            return constructEnumFromSimpleList(yamlInput);
        } else {
            return createEnumFromYamlValues(yamlInput);
        }
    }

    /**
     * Create a string from all entries in a  simple YAML List. The strings are concatenated by ', ' and
     * can be used as an ENUM value in an OpenAPI specification.
     * @param yamlInput a configuration object containing information needed to extract the values from the YAML.
     * @return all values from the given list as a comma separated string.
     */
    private String constructEnumFromSimpleList(YamlResolver yamlInput) {
        try {
            YAML fullYaml = YAML.resolveLayeredConfigs(filePath);
            StringJoiner stringJoiner = new StringJoiner(", ");

            fullYaml.getList(yamlInput.yamlPath)
                    .forEach(string -> stringJoiner.add((CharSequence) string));

            return stringJoiner.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String createEnumFromYamlValues(YamlResolver yamlInput) {
        try {
            YAML fullYaml = YAML.resolveLayeredConfigs(filePath);
            List<String> valuesFromYaml = fullYaml.getMultipleFromSubYaml(yamlInput.yamlPath, yamlInput.key);
            StringJoiner stringJoiner = new StringJoiner(", ");

            valuesFromYaml.forEach(stringJoiner::add);

            return stringJoiner.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extract a single value from a given YAML configuration.
     * @param yamlInput a configuration object containing information needed to extract the values from the YAML.
     * @return the value from the specified path.
     */
    private String getSingleValue(YamlResolver yamlInput) {
        try {
            YAML fullYaml = YAML.resolveLayeredConfigs(filePath);
            return fullYaml.getString(yamlInput.yamlPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add properties extracted from YAML to maven properties.
     * @param propertyName the name of the property.
     * @param propertyValue the value created through this plugin.
     */
    private void addYamlPropertiesToProjectProperties(String propertyName, String propertyValue) {
        project.getProperties().put(propertyName, propertyValue);
        getLog().debug("Added the property: '" + propertyName + "' with the value: '" + propertyValue +
                "' to maven properties."  );
    }
}
