package dk.kb.util;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;

import org.apache.maven.project.MavenProject;
import org.junit.Rule;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class InjectionMavenPluginTest extends AbstractMojoTestCase {
    @Rule
    public MojoRule rule = new MojoRule();

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        // required for mojo lookups to work
        super.setUp();
    }


    @Test
    public void testEnumFromList() throws Exception {
        List<YamlResolver> yamlResolverList = new ArrayList<>();
        YamlResolver yamlInput = createTestYamlInput();
        yamlResolverList.add(yamlInput);

        // Create Mojo
        InjectionMavenPlugin yamlMojo = new InjectionMavenPlugin();

        // Set test values
        MavenProject project = new MavenProject();
        yamlMojo.setProject(project);
        yamlMojo.setYamlResolvers(yamlResolverList);



        // Execute plugin
        yamlMojo.execute();

        //assertEquals(193, yamlMojo.project.getProperties().size());
    }

    private YamlResolver createTestYamlInput(){
        YamlResolver yamlInput = new YamlResolver();
        yamlInput.setFilePath("yamlStructure.yaml");
        yamlInput.setCollectionPath("origins");
        yamlInput.setYamlType("List");
        yamlInput.setCreateEnum(true);
        yamlInput.setKey("origin");
        return yamlInput;
    }
}
