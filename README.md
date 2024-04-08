# Injection Maven Plugin by the Royal Danish Library.

DEPRECATED: This plugin is not needed by the DS-services anymore, as injections from config are handled dynamically. 

This maven plugin provides the capability of injecting properties from configuration files written in YAML into internal
maven properties. 

A use-case for this feature is when developing an OpenAPI api, where values from configuration files are to be used in
the OpenAPI specification. In this case, this plugin makes it possible to specify values in the configuration file and
then reference them in the OpenAPI specification. Furthermore, the plugin comes with a `createEnum` configuration, which
can be used to concatenate multiple values into a single property, which then can be used to create an OpenAPI enum of 
values from the configuration file.

## Requirements
* Maven 3
* Java 11

## Usage
The primary use case for this maven plugin is to inject external properties into the properties of the maven project 
early in the build process, so that these properties can be referenced in other files, such as OpenAPI specifications.

### Configuration of the plugin
To use the plugin in a project add the stub below to the POM of the project, remember to change the version to the 
newest available. When configuring the plugin a couple of parameters can and should be tweaked:
* First of all a valid file path has to be specified.
* Then the `yamlResolvers` has to be populated with a resolver for each property that is to be extracted from the YAML-file.
* Each `YamlResolver` can contain the following values: 
  * yamlType: This should be either 'Sequence', 'List', 'Map' or 'Single-value' and defines what kind of object to extract.
  * yamlPath: This is the path to the property inside the YAML file, see the code documentation or maven tag for usage.
  * key: If extracting from a List, Map, Sequence, Tuple, etc. this key is used to define which element inside the structure to retrieve.
  * createEnum: If an enum is to be created for the given values, set this to true. For further documentation see the javadoc.
```xml
<plugin>
  <groupId>dk.kb.util</groupId>
  <artifactId>injection-maven-plugin</artifactId>
  <version>1.0-SNAPSHOT</version>
  <executions>
    <execution>
      <goals>
        <goal>read-yaml-properties</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <filePath>yamlStructure.yaml</filePath>
    <yamlResolvers>
      <YamlResolver>
        <yamlType>Sequence</yamlType>
        <yamlPath>origins</yamlPath>
        <key>origin</key>
        <createEnum>true</createEnum>
      </YamlResolver>
      <YamlResolver>
        <yamlType>Single-value</yamlType>
        <yamlPath>testvalue.teststring</yamlPath>
      </YamlResolver>
      <YamlResolver>
        <yamlType>List</yamlType>
        <yamlPath>testlist</yamlPath>
        <createEnum>true</createEnum>
      </YamlResolver>
      <YamlResolver>
        <yamlType>Map</yamlType>
        <yamlPath>simplemap</yamlPath>
        <createEnum>true</createEnum>
      </YamlResolver>
      <YamlResolver>
        <yamlType>List</yamlType>
        <yamlPath>allowed_origins</yamlPath>
        <key>name</key>
        <createEnum>true</createEnum>
      </YamlResolver>
    </yamlResolvers>
  </configuration>
</plugin>
```

