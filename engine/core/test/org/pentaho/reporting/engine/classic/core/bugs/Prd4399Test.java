package org.pentaho.reporting.engine.classic.core.bugs;

import java.awt.BasicStroke;
import java.io.IOException;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.HorizontalLineType;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class Prd4399Test extends TestCase
{
  public Prd4399Test()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReport() throws ReportProcessingException, IOException
  {
    MasterReport report = new MasterReport();
    report.getReportHeader().addElement(createLabel());
    report.getReportFooter().addElement(createLabel());
    report.getReportFooter().addElement(createHorizontalLine());

    DebugReportRunner.createPDF(report);
    //PdfReportUtil.createPDF(report, "/tmp/prd-4399-40.pdf");
  }

  private Element createLabel()
  {
    final Element label = new Element();
    label.setElementType(LabelType.INSTANCE);
    label.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, "Label");
    label.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, 100f);
    label.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 20f);
    return label;
  }

  private Element createHorizontalLine()
  {
    final Element label = new Element();
    label.setElementType(HorizontalLineType.INSTANCE);
    label.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, 100f);
    label.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 0f);
    label.getStyle().setStyleProperty(ElementStyleKeys.DRAW_SHAPE, true);
    label.getStyle().setStyleProperty(ElementStyleKeys.STROKE, new BasicStroke(0.2f));
    return label;
  }
}
