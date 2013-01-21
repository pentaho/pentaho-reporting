package org.pentaho.reporting.engine.classic.core.layout.table;

import java.io.File;
import javax.swing.table.DefaultTableModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class PagebreakTest
{
  @Before
  public void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
    new File("test-output").mkdir();
  }

  @Test
  public void testRunSimpleReport() throws Exception
  {
    final MasterReport report = new MasterReport();
    report.setDataFactory(new TableDataFactory("query", new DefaultTableModel(10, 1)));
    report.setQuery("query");

    final Band table = TableTestUtil.createTable(1, 1, 10, true);
    table.setName("table");
    report.getReportHeader().addElement(table);
    report.getReportHeader().setLayout("block");

    PdfReportUtil.createPDF(report, "test-output/output.pdf");
/*
    assertPageValid(report, 0);
    assertPageValid(report, 1);
    assertPageValid(report, 2);
    assertPageValid(report, 3);
    assertPageValid(report, 4);
*/
  }

  private void assertPageValid(final MasterReport report, final int page) throws Exception
  {
    final LogicalPageBox pageBox = DebugReportRunner.layoutPage(report, page);
    final long pageOffset = pageBox.getPageOffset();

    ModelPrinter.print(pageBox);

    final RenderNode[] elementsByNodeType = MatchFactory.findElementsByNodeType(pageBox, LayoutNodeTypes.TYPE_BOX_TABLE_SECTION);
    Assert.assertEquals(2, elementsByNodeType.length);
    final TableSectionRenderBox header = (TableSectionRenderBox) elementsByNodeType[0];
    Assert.assertEquals(TableSectionRenderBox.Role.HEADER, header.getDisplayRole());
    final TableSectionRenderBox body = (TableSectionRenderBox) elementsByNodeType[1];
    Assert.assertEquals(TableSectionRenderBox.Role.BODY, body.getDisplayRole());
    final RenderNode[] rows = MatchFactory.findElementsByNodeType(body, LayoutNodeTypes.TYPE_BOX_TABLE_ROW);
    Assert.assertTrue("Have rows on page " + page, rows.length > 0);

    Assert.assertEquals("Header starts at top of page " + page, pageOffset, header.getY());
    Assert.assertEquals("Row starts after the header on page " + page, header.getY() + header.getHeight(), rows[0].getY());
  }
}
