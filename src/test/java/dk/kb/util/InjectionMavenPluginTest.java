package dk.kb.util;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import org.apache.maven.project.MavenProject;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InjectionMavenPluginTest extends AbstractMojoTestCase {
    protected void setUp() throws Exception {
        // required setup for your test
        super.setUp();
    }

    protected void tearDown() throws Exception {
        // cleanup after your test
        super.tearDown();
    }

    /**
     * @throws Exception if any
     */
    @Test
    public void testParameterInjection() throws Exception {
        File pom = getTestFile( "src/test/resources/project-to-test/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        InjectionMavenPlugin myMojo = (InjectionMavenPlugin) lookupMojo( "read-yaml-properties", pom );
        assertEquals("Sequence",  myMojo.yamlResolvers.get(0).yamlType );

    }

    @Test
    public void testMojo() throws Exception {
        File pom = getTestFile( "src/test/resources/project-to-test/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        InjectionMavenPlugin myMojo = (InjectionMavenPlugin) lookupMojo( "read-yaml-properties", pom );

        myMojo.execute();
    }

    @Test
    public void testEnumFromSequence() throws Exception {
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

        // TODO: Make actual test

        //assertEquals(193, yamlMojo.project.getProperties().size());
    }

    private YamlResolver createTestYamlInput(){
        YamlResolver yamlInput = new YamlResolver();
        yamlInput.setFilePath("yamlStructure.yaml");
        yamlInput.setCollectionPath("origins");
        yamlInput.setYamlType("Sequence");
        yamlInput.setCreateEnum(true);
        yamlInput.setKey("origin");
        return yamlInput;
    }


}
