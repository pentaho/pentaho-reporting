package org.pentaho.reporting.engine.classic.core.crosstab;

import java.io.File;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldenSampleGenerator;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class CrosstabPagebreakTest extends TestCase
{
  public CrosstabPagebreakTest()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testStandardReport() throws Exception
  {
    File file = GoldenSampleGenerator.locateGoldenSampleReport("Prd-3857-001.prpt");
    assertNotNull(file);

    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource res = manager.createDirectly(file, MasterReport.class);
    final MasterReport report = (MasterReport) res.getResource();

    final LogicalPageBox boxP1 = DebugReportRunner.layoutPage(report, 0);
    final LogicalPageBox boxP2 = DebugReportRunner.layoutPage(report, 1);
    ModelPrinter.INSTANCE.print(boxP1);
    final RenderNode[] rowsPage1 = MatchFactory.findElementsByNodeType(boxP1, LayoutNodeTypes.TYPE_BOX_TABLE_ROW);
    assertEquals(23, rowsPage1.length);
//    ModelPrinter.INSTANCE.print(boxP2);
    final RenderNode[] rowsPage2 = MatchFactory.findElementsByNodeType(boxP2, LayoutNodeTypes.TYPE_BOX_TABLE_ROW);
    assertEquals(13, rowsPage2.length);

  }
}
