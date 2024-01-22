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
    public void testReadYaml() throws Exception {
        List<String> files = new ArrayList<>();
        files.add("yamlStructure.yaml");
        InjectionMavenPlugin yamlMojo = new InjectionMavenPlugin();

        MavenProject project = new MavenProject();
        yamlMojo.setProject(project);
        yamlMojo.setFiles(files);

        yamlMojo.execute();

        assertEquals(193, yamlMojo.project.getProperties().size());
    }
}
