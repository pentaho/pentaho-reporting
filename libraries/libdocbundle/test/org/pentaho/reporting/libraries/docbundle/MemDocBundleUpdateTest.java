package org.pentaho.reporting.libraries.docbundle;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class MemDocBundleUpdateTest extends TestCase
{
  public MemDocBundleUpdateTest()
  {
    super();
  }

  public MemDocBundleUpdateTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    LibDocBundleBoot.getInstance().start();
  }

  public void testBug() throws IOException, ResourceException, InterruptedException
  {
    final Properties p1 = new Properties();
    p1.setProperty("key", "value1");

    final MemoryDocumentBundle bundle = new MemoryDocumentBundle();
    bundle.getWriteableDocumentMetaData().setBundleType("text/plain");
    final OutputStream outputStream = bundle.createEntry("test.properties", "text/plain");
    p1.store(outputStream, "run 1");
    outputStream.close();

    final ResourceManager resourceManager = bundle.getResourceManager();
    final ResourceKey key = resourceManager.deriveKey(bundle.getBundleMainKey(), "test.properties");
    final Resource res1 = resourceManager.create(key, null, Properties.class);
    assertEquals(p1, res1.getResource());

    bundle.removeEntry("test.properties");

    Thread.sleep(6000);
    final Properties p2 = new Properties();
    p2.setProperty("key", "value2");

    final OutputStream outputStream2 = bundle.createEntry("test.properties", "text/plain");
    p2.store(outputStream2, "run 2");
    outputStream2.close();

    final Resource res2 = resourceManager.create(key, null, Properties.class);
    assertEquals(p2, res2.getResource());
  }
}
