package edu.illinois.library.cantaloupe.config;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HeritablePropertiesConfigurationTest extends AbstractFileConfigurationTest {

    private HeritablePropertiesConfiguration instance, instance2;

    private HeritablePropertiesConfiguration load(Path path, boolean clear) throws ConfigurationException {
        System.setProperty(ConfigurationFactory.CONFIG_VM_ARGUMENT, path.toString());
        HeritablePropertiesConfiguration instance = new HeritablePropertiesConfiguration();
        instance.reload();
        if (clear) {
          instance.clear();
        }
        return instance;
    }
    
    @Before
    public void setUp() throws Exception {
        super.setUp();

        // Instances won't work without an actual file backing them up.
        File directory = new File(".");
        String cwd = directory.getCanonicalPath();

        instance2 = load(Paths.get(cwd, "src", "test", "resources", "heritable_level1.properties"), false);
        instance = load(Paths.get(cwd, "src", "test", "resources", "heritable_level3.properties"), true);
    }
    
    @Test
    public void testBaseTypes() {
        assertEquals("alpha", instance2.getString("alpha"));
        assertEquals(2, instance2.getInt("bravo"));
        assertEquals(4L, instance2.getLong("charlie"));
        assertEquals(1.5d, instance2.getDouble("delta"), 0);
        assertEquals(4.6, instance2.getFloat("echo"), 0.000001);
        assertFalse(instance2.getBoolean("foxtrot"));
    }
    
    @Test
    public void testInheritedTypes() throws ConfigurationException {
        instance.reload();
        assertEquals("AAA", instance.getString("alpha"));
        assertEquals(20, instance.getInt("bravo"));
        assertEquals(4096L, instance.getLong("charlie"));
        assertEquals(3.5d, instance.getDouble("delta"), 0);
        assertEquals(2.6, instance.getFloat("echo"), 0.000001);
        assertTrue(instance.getBoolean("foxtrot"));
    }

    protected Configuration getInstance() {
        return instance;
    }

    /* getFiles() */

    @Test
    public void testGetFilesReturnsAllFiles() {
        assertEquals(3, instance.getFiles().size());
    }

    /* getKeys() */

    @Test
    public void testGetKeysReturnsKeysFromAllAllFiles() throws Exception {
        instance.reload();

        Iterator<String> it = instance.getKeys();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        assertEquals(21, count);
    }

    /* getProperty(Key) */

    /**
     * Override because this class stores all values internally as strings.
     */
    @Override
    @Test
    public void testGetPropertyWithKeyWithPresentProperty() {
        final Configuration instance = getInstance();
        instance.setProperty(Key.IIIF_1_ENDPOINT_ENABLED, "1");
        instance.setProperty(Key.IIIF_2_ENDPOINT_ENABLED, 2);
        assertEquals("1", instance.getProperty(Key.IIIF_1_ENDPOINT_ENABLED));
        assertEquals("2", instance.getProperty(Key.IIIF_2_ENDPOINT_ENABLED));
        assertNull(instance.getProperty(Key.MAX_PIXELS));
    }

    /* getProperty(String) */

    /**
     * Override because this class stores all values internally as strings.
     */
    @Override
    @Test
    public void testGetPropertyWithStringWithPresentProperty() {
        final Configuration instance = getInstance();
        instance.setProperty("cats", "1");
        instance.setProperty("dogs", 2);
        assertEquals("1", instance.getProperty("cats"));
        assertEquals("2", instance.getProperty("dogs"));
        assertNull(instance.getProperty("pigs"));
    }

    @Test
    public void testGetPropertyUsesChildmostProperty() throws Exception {
        instance.reload();
        assertEquals("birds", instance.getProperty("common_key"));
    }

    @Test
    public void testGetPropertyFallsBackToParentFileIfUndefinedInChildFile()
            throws Exception {
        instance.reload();
        assertEquals("dogs", instance.getProperty("level2_key"));
    }

    /* setProperty() */

    @Test
    public void testSetPropertySetsExistingPropertiesInSameFile()
            throws Exception {
        instance.reload();
        List<PropertiesDocument> docs = instance.getConfigurationTree();
        instance.setProperty("level2_key", "bears");
        assertNull(docs.get(0).get("level2_key"));
        assertEquals("bears", docs.get(1).get("level2_key"));
        assertNull(docs.get(2).get("level2_key"));
    }

    @Test
    public void testSetPropertySetsNewPropertiesInChildmostFile()
            throws Exception {
        instance.reload();
        List<PropertiesDocument> docs = instance.getConfigurationTree();
        instance.setProperty("newkey", "bears");
        assertEquals("bears", docs.get(0).get("newkey"));
        assertNull(docs.get(1).get("newkey"));
        assertNull(docs.get(2).get("newkey"));
    }

    @Test
    public void testGetPropertyReturnsNullIfKeyIsSpecifiedButNoValueIsPresent() throws ConfigurationException {
        instance.reload();

        assertNull(instance.getProperty("key_without_value"));
    }
}
