package org.pentaho.reporting.engine.classic.core.layout;

import java.io.IOException;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

@SuppressWarnings("HardCodedStringLiteral")
public class OrphanTest extends TestCase
{
  public OrphanTest()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
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
    group0.getHeader().addElement(createDataItem("outer-header-field", 100, 20));
    group0.getFooter().addElement(createDataItem("outer-footer-field", 100, 20));
    group0.getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);

    final RelationalGroup group1 = (RelationalGroup) report.getGroup(1);
    group1.setName("inner-group");
    group1.addField("g1");
    group1.getHeader().addElement(createDataItem("inner-header-field", 100, 20));
    group1.getFooter().addElement(createDataItem("inner-footer-field", 100, 20));
    report.getItemBand().addElement(createDataItem("detail-field", 100, 20));
    report.getItemBand().getParentSection().getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);
    group1.getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);

    PdfReportUtil.createPDF(report, "/tmp/OrphanTest.pdf");
  }

  public void testInvalidReport() throws Exception
  {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn("g0", String.class);
    model.addColumn("g1", String.class);
    model.addColumn("value", String.class);
    model.addRow("a", "1", "row-0");
    model.addRow("a", "1", "row-1");
    model.addRow("a", "1", "row-2");
    model.addRow("a", "2", "row-3");
    model.addRow("b", "1", "row-4");
    model.addRow("b", "2", "row-5");
    model.addRow("b", "2", "row-6");
    model.addRow("b", "2", "row-7");
    model.addRow("b", "3", "row-8");
    model.addRow("b", "3", "row-9");
    model.addRow("b", "3", "row-10");
    model.addRow("b", "3", "row-11");
    model.addRow("a", "1", "row-12");
    model.addRow("b", "1", "row-13");
    model.addRow("b", "2", "row-14");

    final MasterReport report = new MasterReport();
    report.setPageDefinition(new SimplePageDefinition(new PageSize(500, 100)));
    report.addGroup(new RelationalGroup());
    report.setDataFactory(new TableDataFactory("query", model));
    report.setQuery("query");

    final RelationalGroup group0 = (RelationalGroup) report.getGroup(0);
    group0.setName("outer-group");
    group0.addField("g0");
    group0.getHeader().addElement(createDataItem("outer-header-field", 100, 20));
    group0.getFooter().addElement(createDataItem("outer-footer-field", 100, 20));
    group0.getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);

    final RelationalGroup group1 = (RelationalGroup) report.getGroup(1);
    group1.setName("inner-group");
    group1.addField("g1");
    group1.getHeader().addElement(createDataItem("inner-header-field", 100, 20));
    group1.getFooter().addElement(createDataItem("inner-footer-field", 100, 20));
    report.getItemBand().addElement(createFieldItem("detail-field", 100, 20));
    report.getItemBand().getParentSection().getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 200);
    group1.getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);

    PdfReportUtil.createPDF(report, "/tmp/OrphanTest2.pdf");

//    ModelPrinter.INSTANCE.print(DebugReportRunner.layoutPage(report, 1));
//    ModelPrinter.INSTANCE.print(DebugReportRunner.layoutPage(report, 2));
  }

  public void testInvalidReport2() throws Exception
  {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn("g0", String.class);
    model.addColumn("g1", String.class);
    model.addColumn("value", String.class);
    model.addRow("a", "1", "row-0");
    model.addRow("a", "1", "row-1");
    model.addRow("a", "1", "row-2");
    model.addRow("a", "2", "row-3");
    model.addRow("b", "1", "row-4");
    model.addRow("b", "2", "row-5");
    model.addRow("b", "2", "row-6");
    model.addRow("b", "2", "row-7");
    model.addRow("b", "3", "row-8");
    model.addRow("b", "3", "row-9");
    model.addRow("b", "3", "row-10");
    model.addRow("b", "3", "row-11");
    model.addRow("b", "3", "row-12");
    model.addRow("a", "1", "row-13");
    model.addRow("b", "1", "row-14");
    model.addRow("b", "2", "row-15");

    final MasterReport report = new MasterReport();
    report.setPageDefinition(new SimplePageDefinition(new PageSize(500, 100)));
    report.addGroup(new RelationalGroup());
    report.setDataFactory(new TableDataFactory("query", model));
    report.setQuery("query");

    final RelationalGroup group0 = (RelationalGroup) report.getGroup(0);
    group0.setName("outer-group");
    group0.addField("g0");
    group0.getHeader().addElement(createDataItem("outer-header-field", 100, 20));
    group0.getFooter().addElement(createDataItem("outer-footer-field", 100, 20));
    group0.getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);

    final RelationalGroup group1 = (RelationalGroup) report.getGroup(1);
    group1.setName("inner-group");
    group1.addField("g1");
    group1.getHeader().addElement(createDataItem("inner-header-field", 100, 20));
    group1.getFooter().addElement(createDataItem("inner-footer-field", 100, 20));
    report.getItemBand().addElement(createFieldItem("detail-field", 100, 20));
    report.getItemBand().getParentSection().getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 200);
    group1.getStyle().setStyleProperty(ElementStyleKeys.ORPHANS, 2);

    PdfReportUtil.createPDF(report, "/tmp/OrphanTest2.pdf");

//    ModelPrinter.INSTANCE.print(DebugReportRunner.layoutPage(report, 4));
//    ModelPrinter.INSTANCE.print(DebugReportRunner.layoutPage(report, 5));
  }

  public static Element createDataItem(final String text, final float width, final float height)
  {
    final Element label = new Element();
    label.setElementType(LabelType.INSTANCE);
    label.setName(text);
    label.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text);
    label.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, width);
    label.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, height);
    return label;
  }

  public static Element createFieldItem(final String text, final float width, final float height)
  {
    final FormulaExpression fe = new FormulaExpression();
    fe.setFormula("=[value]");

    final Element label = new Element();
    label.setElementType(LabelType.INSTANCE);
    label.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text);
    label.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, width);
    label.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, height);
    label.setAttributeExpression(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, fe);
    label.setAttributeExpression(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, fe);
    return label;
  }

  private static void assertElementDoesNotExist(final String element, final RenderBox box)
  {
    final RenderNode ib1 = MatchFactory.findElementByName(box, element);
    assertNull("Element '" + element + "' does NOT exist.", ib1);
  }

  private static void assertElementExists(final String element, final RenderBox box)
  {
    final RenderNode ib1 = MatchFactory.findElementByName(box, element);
    assertNotNull("Element '" + element + "' exists.", ib1);
  }
}
