package dk.kb.util;

import org.apache.maven.plugins.annotations.Parameter;

public class YamlResolver {

    /**
     * This field should contain the YAML path to the given entry, which we want to extract from the YAML.
     * Path elements are separated by {@code .} and can be either:
     * <ul>
     * <li>{@code key} for direct traversal, e.g. "foo" or "foo.bar"</li>
     * <li>{@code key[index]} for a specific element in a list, e.g. "foo.[2]" or "foo.[2].bar"</li>
     * <li>{@code key.[last]} for the last element in a list, e.g. "foo.[last]" or "foo.bar.[last]"</li>
     * <li>{@code key.[subkey=value]} for the first map element in a list where its value for the subkey matches, e.g. "foo.[bar=baz]"</li>
     * <li>{@code key.[subkey!=value]} for the map element in a list where its value for the subkey does not match, e.g. "foo.[bar!=baz]"</li>
     * </ul>
     * Note: Dots {@code .} in YAML keys can be escaped with quotes: {@code foo.'a.b.c' -> [foo, a.b.c]}.
     */
    String yamlPath;

    /**
     * The plugin requires a destination key for yeah resolver. This key determines the key for the property, when
     * extracted to maven properties. This is the key, which should be looked up by the project using this plugin.
     */
    @Parameter(required = true)
    String destinationKey;

    /**
     * If an enum is to be created for the given values, set this to true.
     * When this value is set to true, lists, sequences and maps given as yamlPath are combined into a comma-separated
     * string. For example: A list with the values "one", "two" and "tree" are concatenated to the following string:
     * "one, two, three".
     * When the value isn't set or explicitly set to false, then the property extracted from this {@link YamlResolver}
     * is a single value.
     */
    @Parameter(defaultValue = "false")
    boolean createEnum;

    public void setCreateEnum(boolean createEnum) {
        this.createEnum = createEnum;
    }

    public String getYamlPath() {
        return yamlPath;
    }

    public void setYamlPath(String yamlPath) {
        this.yamlPath = yamlPath;
    }

    public String getDestinationKey() {
        return destinationKey;
    }

    public void setDestinationKey(String destinationKey) {
        this.destinationKey = destinationKey;
    }
}
