package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class InlineSubReportCrashTest extends TestCase
{
  public InlineSubReportCrashTest()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReport() throws Exception
  {
    MasterReport report = new MasterReport();
    report.getReportHeader().addElement(new SubReport());

    // if the bug is there, it will fail with an StackOverflowError ..
    DebugReportRunner.layoutPage(report, 0);
  }
}
