package org.pentaho.reporting.engine.classic.core.bugs;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.XmlPageReportUtil;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class Prd2865Test extends TestCase
{
  public Prd2865Test()
  {
  }

  public Prd2865Test(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRunSample() throws Exception
  {
    final URL url = getClass().getResource("Prd-2865.prpt");
    assertNotNull(url);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.getReportConfiguration().setConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8");
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    XmlPageReportUtil.createXml(report, out);
    final String outText = out.toString("UTF-8");

    System.out.println(outText);
  }
}