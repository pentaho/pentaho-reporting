package org.pentaho.reporting.engine.classic.core.bugs;

import java.io.File;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class Prd3857Test extends TestCase
{
  public Prd3857Test()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testGoldRun () throws Exception
  {
    final File file = GoldTestBase.locateGoldenSampleReport("Prd-3239.prpt");
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly(file, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));

    DebugReportRunner.createXmlFlow(report);

  }

  public void testRowBoxesEstablishOwnBlockContext() throws Exception
  {
    // this report defines that the group as well as all bands within that group are row-layout.
    // therefore the two itembands end on the same row.

    // The itemband did not define a width, not even a 100% width, and thus ends with a width of auto/zero.
    // therefore the itemband shrinks to the minimal size that still encloses all elements.
    // the elements that have percentage width are resolved against the block context.
    // A band without a width defined (the itemband!), does not establish an own block-context, so it
    // takes the block context of the parent, or as fallback: page.

    final File file = GoldTestBase.locateGoldenSampleReport("Prd-3479.prpt");
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly(file, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel(null);

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(report, 0);
    final RenderNode[] itembands = MatchFactory.findElementsByElementType(logicalPageBox, ItemBandType.INSTANCE);

    assertEquals(2, itembands.length);
    assertEquals(48208843, itembands[0].getWidth());
    assertEquals(48208843, itembands[1].getWidth());
    assertEquals(48208843, itembands[1].getX());
  }
}
