package org.pentaho.reporting.engine.classic.core.layout;

import java.io.IOException;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.layout.table.TableTestUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

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
    final RelationalGroup group1 = (RelationalGroup) report.getGroup(1);
    group1.setName("inner-group");
    group1.addField("g1");
    group1.getHeader().addElement(TableTestUtil.createDataItem("inner-header-field", 100, 20));
    group1.getFooter().addElement(TableTestUtil.createDataItem("inner-footer-field", 100, 20));
    report.getItemBand().addElement(TableTestUtil.createDataItem("detail-field", 100, 20));
    report.getItemBand().getParentSection().getStyle().setStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, Boolean.TRUE);
    group1.getParentSection().getStyle().setStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, Boolean.TRUE);

    PdfReportUtil.createPDF(report, "/tmp/WidowTest.pdf");
  }
}
