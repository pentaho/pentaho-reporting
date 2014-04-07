package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class Prd4928Test
{
  @Before
  public void setUp()
  {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testGoldenSample() throws Exception
  {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport("Prd-4928.prpt");
    report.getItemBand().getStyle().setStyleProperty(ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, false);
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(report, 0);

    ModelPrinter.INSTANCE.print(logicalPageBox);
  }
}
