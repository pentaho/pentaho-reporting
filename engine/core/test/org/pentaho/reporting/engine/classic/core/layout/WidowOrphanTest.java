package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.table.TableTestUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public class WidowOrphanTest extends TestCase
{
  public WidowOrphanTest()
  {
  }

  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testStandardLayout() throws ReportProcessingException, ContentProcessingException
  {
    MasterReport report = new MasterReport();

    final Band detailBody = new Band();
    detailBody.setLayout("block");
    detailBody.setName("detail-body");
    detailBody.addElement(createBand("ib1"));
    detailBody.addElement(createBand("ib2"));
    detailBody.addElement(createBand("ib3"));

    final Band insideGroup = new Band();
    insideGroup.getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);
    insideGroup.getStyle().setStyleProperty(ElementStyleKeys.WIDOWS, 2);
    insideGroup.setLayout("block");
    insideGroup.setName("group-inside");
    insideGroup.addElement(createBand("group-header-inside"));
    insideGroup.addElement(detailBody);
    insideGroup.addElement(createBand("group-footer-inside"));

    final Band outsideBody = new Band();
    outsideBody.setLayout("block");
    outsideBody.setName("group-body-outside");
    outsideBody.addElement(insideGroup);

    final ReportHeader band = report.getReportHeader();
    band.setLayout("block");
    band.setName("group-outside");
    band.getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);
    band.getStyle().setStyleProperty(ElementStyleKeys.WIDOWS, 2);
    band.addElement(createBand("group-header-outside"));
    band.addElement(outsideBody);
    band.addElement(createBand("group-footer-outside"));

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand(report, band, false, false);
    final RenderNode grOut = MatchFactory.findElementByName(logicalPageBox, "group-outside");
    assertTrue(grOut instanceof RenderBox);
    final RenderBox grOutBox = (RenderBox) grOut;
    assertEquals (StrictGeomUtility.toInternalValue(60), grOutBox.getOrphanConstraintSize());
    assertEquals (StrictGeomUtility.toInternalValue(60), grOutBox.getWidowConstraintSize());

    final RenderNode grIn = MatchFactory.findElementByName(logicalPageBox, "group-inside");
    assertTrue(grIn instanceof RenderBox);
    final RenderBox grInBox = (RenderBox) grIn;
    assertEquals(StrictGeomUtility.toInternalValue(40), grInBox.getOrphanConstraintSize());
    assertEquals(StrictGeomUtility.toInternalValue(40), grInBox.getWidowConstraintSize());

    // ModelPrinter.INSTANCE.print(logicalPageBox);
  }

  private Band createBand (String name)
  {
    final Band ghO1 = new Band();
    ghO1.setName(name);
    ghO1.addElement(TableTestUtil.createDataItem(name, 100, 20));
    return ghO1;
  }
}
