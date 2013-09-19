package org.pentaho.reporting.libraries.resourceloader;

import java.awt.Image;
import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

public class SVGLoadingTest extends TestCase
{
  public SVGLoadingTest()
  {
  }

  public SVGLoadingTest(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    LibLoaderBoot.getInstance().start();
  }

  public void testSVGLoading() throws ResourceException
  {
    final URL resource = SVGLoadingTest.class.getResource("SVG.svg");
    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource directly = manager.createDirectly(resource, DrawableWrapper.class);
    assertNotNull(directly.getResource());
  }

  public void testSVGLoading2() throws ResourceException
  {
    final URL resource = SVGLoadingTest.class.getResource("SVG.svg");
    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource directly = manager.createDirectly(resource, Image.class);
    assertNotNull(directly.getResource());
  }
}
