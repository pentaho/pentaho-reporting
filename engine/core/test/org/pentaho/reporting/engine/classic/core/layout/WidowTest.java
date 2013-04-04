package org.pentaho.reporting.engine.classic.core.layout;

import java.io.IOException;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.table.TableTestUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

@SuppressWarnings("HardCodedStringLiteral")
public class WidowTest extends TestCase
{
  public WidowTest()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testStandardLayout() throws ReportProcessingException, ContentProcessingException
  {
    final MasterReport report = new MasterReport();
    report.setPageDefinition(new SimplePageDefinition(new PageSize(500, 100)));

    final Band detailBody = new Band();
    detailBody.setLayout("block");
    detailBody.setName("detail-body-1");
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

    final Band detailBody2 = new Band();
    detailBody2.setLayout("block");
    detailBody2.setName("detail-body-1");
    detailBody2.addElement(createBand("ib1"));
    detailBody2.addElement(createBand("ib2"));
    detailBody2.addElement(createBand("ib3"));

    final Band insideGroup2 = new Band();
    insideGroup2.getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);
    insideGroup2.getStyle().setStyleProperty(ElementStyleKeys.WIDOWS, 2);
    insideGroup2.setLayout("block");
    insideGroup2.setName("group-inside");
    insideGroup2.addElement(createBand("group-header-inside"));
    insideGroup2.addElement(detailBody2);
    insideGroup2.addElement(createBand("group-footer-inside"));

    final Band outsideBody = new Band();
    outsideBody.setLayout("block");
    outsideBody.setName("group-body-outside");
    outsideBody.addElement(insideGroup);
    outsideBody.addElement(insideGroup2);

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

    ModelPrinter.INSTANCE.print(logicalPageBox);
  }

  public void testReport() throws ReportProcessingException, IOException
  {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn("g0", String.class);
    model.addColumn("g1", String.class);
    model.addColumn("value", String.class);
    model.addRow("a", "1", "row-0");
    model.addRow("a", "2", "row-1");
    model.addRow("b", "1", "row-2");
    model.addRow("b", "2", "row-3");
    model.addRow("b", "2", "row-4");
    model.addRow("b", "2", "row-5");
    model.addRow("b", "3", "row-6");
    model.addRow("a", "1", "row-7");
    model.addRow("b", "1", "row-8");
    model.addRow("b", "2", "row-9");

    final MasterReport report = new MasterReport();
    report.setPageDefinition(new SimplePageDefinition(new PageSize(500, 100)));
    report.addGroup(new RelationalGroup());
    report.setDataFactory(new TableDataFactory("query", model));
    report.setQuery("query");

    final RelationalGroup group0 = (RelationalGroup) report.getGroup(0);
    group0.setName("outer-group");
    group0.addField("g0");
    group0.getHeader().addElement(TableTestUtil.createDataItem("outer-header-field", 100, 20));
    group0.getFooter().addElement(TableTestUtil.createDataItem("outer-footer-field", 100, 20));
    group0.getStyle().setStyleProperty(ElementStyleKeys.WIDOWS, 2);

    final RelationalGroup group1 = (RelationalGroup) report.getGroup(1);
    group1.setName("inner-group");
    group1.addField("g1");
    group1.getHeader().addElement(TableTestUtil.createDataItem("inner-header-field", 100, 20));
    group1.getFooter().addElement(TableTestUtil.createDataItem("inner-footer-field", 100, 20));
    group1.getStyle().setStyleProperty(ElementStyleKeys.WIDOWS, 2);

    report.getItemBand().addElement(TableTestUtil.createDataItem("detail-field", 100, 20));
    report.getItemBand().getParentSection().getStyle().setStyleProperty(ElementStyleKeys.WIDOWS, 2);

    PdfReportUtil.createPDF(report, "/tmp/WidowTest.pdf");
  }

  private Band createBand(final String name)
  {
    return createBand(name, 20);
  }

  private Band createBand(final String name, final float height)
  {
    final Band ghO1 = new Band();
    ghO1.setName(name);
    ghO1.getStyle().setStyleProperty(ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, false);
    ghO1.addElement(TableTestUtil.createDataItem(name, 100, height));
    return ghO1;
  }
}
