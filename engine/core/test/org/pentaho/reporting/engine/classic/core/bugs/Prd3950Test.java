package org.pentaho.reporting.engine.classic.core.bugs;

import java.io.File;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class Prd3950Test extends TestCase
{
  public Prd3950Test()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testGoldRun () throws Exception
  {
    File file = GoldTestBase.locateGoldenSampleReport("Prd-3950.prpt");
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly(file, MasterReport.class);
    MasterReport report = (MasterReport) directly.getResource();

    DebugReportRunner.createXmlTablePageable(report);
  }
}
