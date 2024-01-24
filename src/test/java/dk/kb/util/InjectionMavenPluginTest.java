package dk.kb.util;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import org.apache.maven.project.MavenProject;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InjectionMavenPluginTest extends AbstractMojoTestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }
    protected void tearDown() throws Exception {
        super.tearDown();
    }

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
        // Test execution from test pom
        File pom = getTestFile( "src/test/resources/project-to-test/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );
        InjectionMavenPlugin myMojo = (InjectionMavenPlugin) lookupMojo( "read-yaml-properties", pom );
        myMojo.execute();
    }

    @Test
    public void testEnumFromSequence() throws Exception {
        YamlResolver testSequence = createTestSequence();
        InjectionMavenPlugin yamlMojo = setupTestProject(testSequence);
        // Execute plugin
        yamlMojo.execute();
        assertEquals("kb.images.billed.hca, kb.image.judsam.jsmss, kb.images.luftfo.luftfoto, kb.pamphlets.dasmaa.smaatryk, ds.samlingsbilleder, ds.radiotv, ds.radio, ds.tv",
                    yamlMojo.project.getProperties().getProperty("origins"));
    }
    @Test
    public void testEnumFromList() throws Exception {
        YamlResolver testList = createTestList();
        InjectionMavenPlugin yamlMojo = setupTestProject(testList);
        // Execute plugin
        yamlMojo.execute();
        assertEquals("one, two, three, four, five",
                    yamlMojo.project.getProperties().getProperty("testlist"));
    }
    @Test
    public void testEnumFromMap() throws Exception {
        YamlResolver testMap = createTestMap();
        InjectionMavenPlugin yamlMojo = setupTestProject(testMap);
        // Execute plugin
        yamlMojo.execute();
        assertEquals("gorm, harald, svend, harald, knud, hardeknud",
                    yamlMojo.project.getProperties().getProperty("simplemap"));
    }

    @Test
    public void testSingleValue() throws Exception {
        YamlResolver testValue = createTestSinglevalue();
        InjectionMavenPlugin yamlMojo = setupTestProject(testValue);
        // Execute plugin
        yamlMojo.execute();
        assertEquals("This is a test string.",
                    yamlMojo.project.getProperties().getProperty("testvalue.teststring"));
    }

    @Test
    public void testComplexList() throws Exception {
        YamlResolver testList = createComplexTestList();
        InjectionMavenPlugin yamlMojo = setupTestProject(testList);
        // Execute plugin
        yamlMojo.execute();
        assertEquals("ds.tv, ds.radio, ds.radiotv6, doms.radio, doms.aviser, origin.strategy.none, origin.strategy.all, origin.strategy.child, origin.strategy.parent, ds.radiotv, ds.radiotv6, ds.samlingsbilleder, ds.maps, kb.manus.vmanus.ha",
                    yamlMojo.project.getProperties().getProperty("allowed_origins"));
    }


    /* METHODS USED TO SETUP TESTS AND ASSERT ON PROJECT PROPERTIES. */
    private InjectionMavenPlugin setupTestProject(YamlResolver yamlInput) {
        List<YamlResolver> yamlResolverList = new ArrayList<>();
        yamlResolverList.add(yamlInput);

        // Create Mojo
        InjectionMavenPlugin yamlMojo = new InjectionMavenPlugin();

        // Set test values
        MavenProject project = new MavenProject();
        yamlMojo.setProject(project);
        yamlMojo.setYamlResolvers(yamlResolverList);

        return yamlMojo;
    }

    private YamlResolver createTestSequence(){
        YamlResolver yamlInput = new YamlResolver();
        yamlInput.setFilePath("yamlStructure.yaml");
        yamlInput.setYamlPath("origins");
        yamlInput.setYamlType("Sequence");
        yamlInput.setCreateEnum(true);
        yamlInput.setKey("origin");
        return yamlInput;
    }
    private YamlResolver createTestList(){
        YamlResolver yamlInput = new YamlResolver();
        yamlInput.setFilePath("yamlStructure.yaml");
        yamlInput.setYamlPath("testlist");
        yamlInput.setYamlType("List");
        yamlInput.setCreateEnum(true);
        return yamlInput;
    }
    private YamlResolver createTestSinglevalue(){
        YamlResolver yamlInput = new YamlResolver();
        yamlInput.setFilePath("yamlStructure.yaml");
        yamlInput.setYamlPath("testvalue.teststring");
        yamlInput.setYamlType("Single-value");
        return yamlInput;
    }

    private YamlResolver createTestMap(){
        YamlResolver yamlInput = new YamlResolver();
        yamlInput.setFilePath("yamlStructure.yaml");
        yamlInput.setYamlPath("simplemap");
        yamlInput.setYamlType("Map");
        yamlInput.setCreateEnum(true);
        return yamlInput;
    }
    private YamlResolver createComplexTestList(){
        YamlResolver yamlInput = new YamlResolver();
        yamlInput.setFilePath("yamlStructure.yaml");
        yamlInput.setYamlPath("allowed_origins");
        yamlInput.setYamlType("List");
        yamlInput.setKey("name");
        yamlInput.setCreateEnum(true);
        return yamlInput;
    }









}
