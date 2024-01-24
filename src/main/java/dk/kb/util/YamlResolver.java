package dk.kb.util;

import org.apache.maven.plugins.annotations.Parameter;

public class YamlResolver {
    /**
     * The path to the input YAML file.
     */
    String filePath;

    /**
     * The type of YAML object to fetch from the file. Currently, supports: List, Sequence, Map and Single-value.
     */
    String yamlType;

    /**
     * This field should contain the YAML path to the given entry, which we want to extract from the YAML.
     */
    String yamlPath;

    /**
     * If yamlType is Map or List, then this field should contain the key or entry-number to look for in the given map.
     */
    @Parameter(defaultValue = "")
    String key;

    /**
     * If an enum is to be created for the given values, set this to true.
     */
    @Parameter(defaultValue = "false")
    boolean createEnum;

    public void setCreateEnum(boolean createEnum) {
        this.createEnum = createEnum;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getYamlType() {
        return yamlType;
    }

    public void setYamlType(String yamlType) {
        this.yamlType = yamlType;
    }

    public String getYamlPath() {
        return yamlPath;
    }

    public void setYamlPath(String yamlPath) {
        this.yamlPath = yamlPath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
