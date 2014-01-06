package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class Prd4849Test extends TestCase
{
  public Prd4849Test()
  {
  }

  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPageFooterExists() throws Exception
  {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport("Prd-4849.prpt");
    report.getPageFooter().setName("PRD-4849");

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(report, 0);
    ModelPrinter.INSTANCE.print(logicalPageBox);
    RenderNode elementByName = MatchFactory.findElementByName(logicalPageBox, "PRD-4849");
    assertNotNull(elementByName);
  }
}
