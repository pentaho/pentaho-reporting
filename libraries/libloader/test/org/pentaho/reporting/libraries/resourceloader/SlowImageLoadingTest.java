package org.pentaho.reporting.libraries.resourceloader;

import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;

public class SlowImageLoadingTest extends TestCase
{
  public SlowImageLoadingTest()
  {
  }

  // This test case relies on a dummy servlet to test caching in libLoader.  Comment out
  // due to test app server dependencies
  public void testLoad() throws Exception
  {
//    final HierarchicalConfiguration globalConfig = (HierarchicalConfiguration) LibLoaderBoot.getInstance().getGlobalConfig();
//    globalConfig.setConfigProperty("org.pentaho.reporting.libraries.resourceloader.config.url.FixedCacheDelay", "0");
//    globalConfig.setConfigProperty("org.pentaho.reporting.libraries.resourceloader.config.url.FixBrokenWebServiceDateHeader", "true");
//    ResourceManager mgr = new ResourceManager();
//    mgr.registerDefaults();
//    System.out.println("Start");
//    mgr.load(mgr.createKey(new URL("http://localhost:8080/test")));
//    System.out.println("load");
//    mgr.load(mgr.createKey(new URL("http://localhost:8080/test")));
//    System.out.println("reload");
//    mgr.load(mgr.createKey(new URL("http://localhost:8080/test")));
//    System.out.println("reload");
  }
}
