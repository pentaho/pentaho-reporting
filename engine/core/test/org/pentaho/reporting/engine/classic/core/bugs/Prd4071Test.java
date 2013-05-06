package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class Prd4071Test extends TestCase
{
  public Prd4071Test()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testExcelExport() throws Exception
  {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport("Prd-4071-Standalone.prpt");
    report.getItemBand().getElement(0).getStyle().setStyleProperty(ElementStyleKeys.DYNAMIC_HEIGHT, true);
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(report, 0);
    ModelPrinter.INSTANCE.print(logicalPageBox);
    assertEquals(64800000, logicalPageBox.getPageEnd());
    final RenderNode[] elementsByElementType = MatchFactory.findElementsByElementType(logicalPageBox, ItemBandType.INSTANCE);
    assertEquals(7, elementsByElementType.length);
    final RenderNode lastChild = elementsByElementType[6];
    assertEquals(64100000, lastChild.getY() + lastChild.getHeight());
  }
}
