package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupFooterType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class Prd4471Test extends TestCase
{
  public Prd4471Test()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReportRun() throws Exception
  {
    final MasterReport elements = DebugReportRunner.parseGoldenSampleReport("Prd-4471.prpt");
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(elements, 0);
    ModelPrinter.INSTANCE.print(logicalPageBox);
    final RenderNode[] elementsByElementType = MatchFactory.findElementsByElementType
        (logicalPageBox.getRepeatFooterArea(), GroupFooterType.INSTANCE);
    assertEquals(0, elementsByElementType.length);
  }
}
