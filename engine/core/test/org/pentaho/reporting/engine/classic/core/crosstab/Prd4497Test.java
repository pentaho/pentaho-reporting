package org.pentaho.reporting.engine.classic.core.crosstab;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class Prd4497Test extends TestCase
{
  public Prd4497Test()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testEmptyPageGenerated() throws Exception
  {
    // this report should generate only two pages of content. With the bug still active, it generates 3.
    MasterReport report = DebugReportRunner.parseGoldenSampleReport("Prd-4497.prpt");
    assertEquals(2, DebugReportRunner.execGraphics2D(report));
  }
}
