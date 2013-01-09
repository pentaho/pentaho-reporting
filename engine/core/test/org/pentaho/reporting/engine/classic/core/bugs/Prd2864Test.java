package org.pentaho.reporting.engine.classic.core.bugs;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class Prd2864Test extends TestCase
{
  public Prd2864Test()
  {
  }

  public Prd2864Test(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRunSample() throws Exception
  {
    final URL url = getClass().getResource("Prd-2849.prpt");
    assertNotNull(url);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.getReportConfiguration().setConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8");
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    HtmlReportUtil.createZIPHTML(report, out, "report.html");
//    final String outText = out.toString("UTF-8");
//    assertTrue(outText.indexOf("<p>Here is my HTML.</p>") > 0);
/*
    int count = 0;
    int occurence = -1;
    do
    {
      occurence = outText.indexOf(">Label</text>", occurence + 1);
      if (occurence > -1)
      {
        count += 1;
      }
    }
    while (occurence != -1);
    assertEquals(2, count);
    */
   // System.out.println(outText);
  }
}