package dk.kb.util;

import org.apache.maven.plugins.annotations.Parameter;

public class YamlResolver {
    /**
     * The path to the input YAML file.
     */
    String filePath;
    /**
     * The type of YAML object to fetch from the file. Currently supports: List, Map and Single-value.
     */
    String yamlType;

    /**
     * If yamlType is Sinlgle-value, then this field should contain the YAML path to the value.
     */
    String singleValuePath;
    /**
     * If yamlType is either List or Map, this field should contain the YAML path to the given list or map.
     */
    String collectionPath;
    /**
     * If yamlType is Map or List, then this field should contain the key or entry-number to look for in the given map.
     */
    String key;
    /**
     * If an enum is to be created for the given values, set this to true.
     */
    @Parameter(defaultValue = "false")
    boolean createEnum;

    public boolean isCreateEnum() {
        return createEnum;
    }

    public void setCreateEnum(boolean createEnum) {
        this.createEnum = createEnum;
    }

    public String getCollectionPath() {
        return collectionPath;
    }

    public void setCollectionPath(String collectionPath) {
        this.collectionPath = collectionPath;
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

    public String getSingleValuePath() {
        return singleValuePath;
    }

    public void setSingleValuePath(String singleValuePath) {
        this.singleValuePath = singleValuePath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
