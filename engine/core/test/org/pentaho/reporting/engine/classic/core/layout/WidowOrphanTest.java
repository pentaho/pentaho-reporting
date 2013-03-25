package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.table.TableTestUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

@SuppressWarnings({"HardCodedStringLiteral", "AutoBoxing"})
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
    final MasterReport report = new MasterReport();
    report.setPageDefinition(new SimplePageDefinition(new PageSize(500, 100)));

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
    band.getStyle().setStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false);
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
    assertEquals(StrictGeomUtility.toInternalValue(60), grOutBox.getOrphanConstraintSize());
    assertEquals(StrictGeomUtility.toInternalValue(60), grOutBox.getWidowConstraintSize());

    final RenderNode grIn = MatchFactory.findElementByName(logicalPageBox, "group-inside");
    assertTrue(grIn instanceof RenderBox);
    final RenderBox grInBox = (RenderBox) grIn;
    assertEquals(StrictGeomUtility.toInternalValue(40), grInBox.getOrphanConstraintSize());
    assertEquals(StrictGeomUtility.toInternalValue(40), grInBox.getWidowConstraintSize());

    // ModelPrinter.INSTANCE.print(logicalPageBox);
  }


  public void testStandardLayoutPageBreak() throws Exception
  {
    final MasterReport report = new MasterReport();
    report.setPageDefinition(new SimplePageDefinition(new PageSize(500, 100)));

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

    final Band band = new Band();
    band.getStyle().setStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false);
    band.setLayout("block");
    band.setName("group-outside");
    band.getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);
    band.getStyle().setStyleProperty(ElementStyleKeys.WIDOWS, 2);
    band.addElement(createBand("group-header-outside"));
    band.addElement(outsideBody);
    band.addElement(createBand("group-footer-outside"));

    final ReportHeader header = report.getReportHeader();
    header.setLayout("block");
    header.addElement(createBand("placeholder", 60));
    header.addElement(band);

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(report, 0);
    // if keep-together works, then we avoid the pagebreak between the inner-group-header and the first itemband.
    // therefore the first page only contains the placeholder element.
    final RenderNode grHOut = MatchFactory.findElementByName(logicalPageBox, "group-header-outside");
    assertNull(grHOut);
    final RenderNode grOut = MatchFactory.findElementByName(logicalPageBox, "group-outside");
    assertNull(grOut);

    final LogicalPageBox logicalPageBox2 = DebugReportRunner.layoutPage(report, 1);
    ModelPrinter.INSTANCE.print(logicalPageBox2);
    final RenderNode grHOut2 = MatchFactory.findElementByName(logicalPageBox2, "group-header-outside");
    assertNotNull(grHOut2);
    final RenderNode grOut2 = MatchFactory.findElementByName(logicalPageBox2, "group-outside");
    assertNotNull(grOut2);
    final RenderNode ib3 = MatchFactory.findElementByName(logicalPageBox2, "ib3");
    assertNotNull(ib3);
  }

  public void testKeepTogetherEffect() throws ReportProcessingException, ContentProcessingException
  {
    final MasterReport report = new MasterReport();
    report.setPageDefinition(new SimplePageDefinition(new PageSize(500, 100)));

    final Band detailBody = new Band();
    detailBody.setLayout("block");
    detailBody.setName("detail-body");
    detailBody.getStyle().setStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, true);
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
    band.getStyle().setStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false);
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
    assertEquals(StrictGeomUtility.toInternalValue(100), grOutBox.getOrphanConstraintSize());
    assertEquals(StrictGeomUtility.toInternalValue(100), grOutBox.getWidowConstraintSize());

    final RenderNode grIn = MatchFactory.findElementByName(logicalPageBox, "group-inside");
    assertTrue(grIn instanceof RenderBox);
    final RenderBox grInBox = (RenderBox) grIn;
    assertEquals(StrictGeomUtility.toInternalValue(80), grInBox.getOrphanConstraintSize());
    assertEquals(StrictGeomUtility.toInternalValue(80), grInBox.getWidowConstraintSize());
  }


  public void testKeepTogetherEffectPagebreak() throws Exception
  {
    final MasterReport report = new MasterReport();
    report.setPageDefinition(new SimplePageDefinition(new PageSize(500, 100)));

    final Band detailBody = new Band();
    detailBody.setLayout("block");
    detailBody.setName("detail-body");
    detailBody.getStyle().setStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, true);
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
    band.getStyle().setStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false);
    band.setLayout("block");
    band.setName("group-outside");
    band.getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);
    band.getStyle().setStyleProperty(ElementStyleKeys.WIDOWS, 2);
    band.addElement(createBand("group-header-outside"));
    band.addElement(outsideBody);
    band.addElement(createBand("group-footer-outside"));

    // when we have a keep-together, the system would naively shift the details-body to the next page.
    // we need to prevent that by claiming that this section belongs to the same widow/orphan group as their parents.
    // they all shift together ..
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand(report, band, false, false);
    final RenderNode grOut = MatchFactory.findElementByName(logicalPageBox, "group-outside");
    assertTrue(grOut instanceof RenderBox);
    final RenderBox grOutBox = (RenderBox) grOut;
    assertEquals(StrictGeomUtility.toInternalValue(60), grOutBox.getOrphanConstraintSize());
    assertEquals(StrictGeomUtility.toInternalValue(60), grOutBox.getWidowConstraintSize());

    final RenderNode grIn = MatchFactory.findElementByName(logicalPageBox, "group-inside");
    assertTrue(grIn instanceof RenderBox);
    final RenderBox grInBox = (RenderBox) grIn;
    assertEquals(StrictGeomUtility.toInternalValue(40), grInBox.getOrphanConstraintSize());
    assertEquals(StrictGeomUtility.toInternalValue(40), grInBox.getWidowConstraintSize());
  }

  private Band createBand(final String name)
  {
    return createBand(name, 20);
  }

  private Band createBand(final String name, final float height)
  {
    final Band ghO1 = new Band();
    ghO1.setName(name);
    ghO1.addElement(TableTestUtil.createDataItem(name, 100, height));
    return ghO1;
  }
}
