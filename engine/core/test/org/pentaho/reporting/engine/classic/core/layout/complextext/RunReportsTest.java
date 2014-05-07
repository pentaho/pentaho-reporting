package org.pentaho.reporting.engine.classic.core.layout.complextext;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.MessageType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.NoCloseOutputStream;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;

public class RunReportsTest extends TestCase
{
  public RunReportsTest()
  {
  }

  public void setUp()
  {
    ClassicEngineBoot.getInstance().start();
  }

  /**
   * This test shows how to validate the layout code easily, without having to start a full report processing
   * run or having to worry about the actual output target implementations.
   *
   * @throws ResourceException
   * @throws ReportProcessingException
   * @throws ContentProcessingException
   */
  public void testLayoutSingleBand() throws ResourceException, ReportProcessingException, ContentProcessingException
  {
    // parse an existing report. You can create reports either via PRD or you can
    // produce them via the API.
    //
    // When you use PRD, only the "SampleData" datasource is available, or you can hardcode values via
    // the table-datasource

    MasterReport report = DebugReportRunner.parseGoldenSampleReport("Prd-3529.prpt");
    // to enable the complex-processing mode, set this configuration option to true
    report.getReportConfiguration().setConfigProperty
        (ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true");

    ReportHeader reportHeader = report.getReportHeader();
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand(report, reportHeader);

    // this creates a print-out of the layout. This is great to quickly see what the layouter produces.
    ModelPrinter.INSTANCE.print(logicalPageBox);

    // use the MatchFactory to quickly locate elements inside the layout model
    RenderNode[] elementsByElementType = MatchFactory.findElementsByElementType(logicalPageBox, MessageType.INSTANCE);
    for (int i = 0; i < elementsByElementType.length; i++)
    {
      RenderNode renderNode = elementsByElementType[i];
      ModelPrinter.INSTANCE.print(renderNode);
    }
  }

  public void testHTMLExport() throws ReportProcessingException, ResourceException
  {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport("Prd-3529.prpt");
    // produce HTML output ..
    HtmlReportUtil.createStreamHTML(report, new NoCloseOutputStream(System.out));
  }

  public void testSwingPrintPreview() throws ReportProcessingException, ResourceException
  {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport("Prd-3529.prpt");
    // produce a print preview. This produces a Graphics2D output which is also used for printing.
    DebugReportRunner.showDialog(report);
  }
}
