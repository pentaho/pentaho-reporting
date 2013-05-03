package org.pentaho.reporting.designer;

import java.io.PrintStream;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.ReportLayouter;
import org.pentaho.reporting.designer.core.editor.report.layouting.SharedElementRenderer;
import org.pentaho.reporting.designer.testsupport.TableTestUtil;
import org.pentaho.reporting.designer.testsupport.TestReportDesignerContext;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;

public class Prd4313Test extends TestCase
{
  public void setUp ()
  {
    final PrintStream out = System.out;
    final PrintStream err = System.err;
    ClassicEngineBoot.getInstance().start();
    System.setOut(out);
    System.setErr(err);
  }

  public void testEventNotification()
  {
    final MasterReport report = new MasterReport();
    final Element mrLabel = TableTestUtil.createDataItem("Label");
    report.getPageHeader().addElement(mrLabel);

    final Element mrLabel2 = TableTestUtil.createDataItem("Label2");
    report.getPageHeader().addElement(mrLabel2);

    final TestReportDesignerContext designerContext = new TestReportDesignerContext();
    final int idx = designerContext.addMasterReport(report);
    final ReportRenderContext masterContext = designerContext.getReportRenderContext(idx);
//    final int srIdx = designerContext.addSubReport(masterContext, subReport);
//    final ReportRenderContext subContext = designerContext.getReportRenderContext(srIdx);


    final SharedElementRenderer sharedRenderer = masterContext.getSharedRenderer();
    assertTrue(sharedRenderer.performLayouting());

    ModelPrinter.INSTANCE.print(sharedRenderer.getPageBox());

    // we should have conflicts ..
  //  assertFalse(sharedRenderer.getConflicts().isEmpty());
  }
}
