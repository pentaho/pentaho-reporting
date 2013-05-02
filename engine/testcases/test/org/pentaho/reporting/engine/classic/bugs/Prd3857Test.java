package org.pentaho.reporting.engine.classic.bugs;

import java.io.File;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.engine.classic.testcases.FixAllBrokenLogging;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class Prd3857Test extends TestCase
{
  public Prd3857Test()
  {
  }

  protected void setUp() throws Exception
  {
    FixAllBrokenLogging.fixBrokenLogging();
    ClassicEngineBoot.getInstance().start();
  }

  public void testGoldRun2 () throws Exception
  {
    File file = GoldTestBase.locateGoldenSampleReport("2sql-subreport.prpt");
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly(file, MasterReport.class);
    MasterReport report = (MasterReport) directly.getResource();

    report.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));

    DebugReportRunner.createXmlFlow(report);
  }

  public void testGoldRun3 () throws Exception
  {
    File file = GoldTestBase.locateGoldenSampleReport("trafficlighting.xml");
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly(file, MasterReport.class);
    MasterReport report = (MasterReport) directly.getResource();

    report.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));

    DebugReportRunner.createXmlFlow(report);
  }

  public void testGoldRun4 () throws Exception
  {
    File file = GoldTestBase.locateGoldenSampleReport("Income Statement.xml");
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly(file, MasterReport.class);
    MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel(null);

    Band element = (Band) report.getReportHeader().getElement(2);
    element.setName("Tester");
    element.getElement(0).setName("m1");
    report.getReportHeader().getElement(3).setName("image");

    final LogicalPageBox pageBox = DebugReportRunner.layoutSingleBand(report, report.getReportHeader(), false, false);
    ModelPrinter.INSTANCE.print(pageBox);

    RenderNode m1 = MatchFactory.findElementByName(pageBox, "m1");
    assertEquals(StrictGeomUtility.toInternalValue(234), m1.getX());
    RenderNode img = MatchFactory.findElementByName(pageBox, "image");
    assertEquals(StrictGeomUtility.toInternalValue(0), img.getX());
    assertEquals(StrictGeomUtility.toInternalValue(234), img.getWidth());
  }

  public void testGoldRun5 () throws Exception
  {
    File file = GoldTestBase.locateGoldenSampleReport("Income Statement.xml");
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly(file, MasterReport.class);
    MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel(null);

    Band element = (Band) report.getReportHeader().getElement(2);
    element.setName("Tester");
    element.getElement(0).setName("m1");

    DebugReportRunner.layoutSingleBand(report, report.getReportHeader());

    final LogicalPageBox pageBox = DebugReportRunner.layoutPage(report, 0);
    RenderNode m1 = MatchFactory.findElementByName(pageBox, "m1");
    assertEquals(StrictGeomUtility.toInternalValue(234), m1.getX());
  }

  public void testGoldRun5a () throws Exception
  {
    File file = GoldTestBase.locateGoldenSampleReport("Income Statement.xml");
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly(file, MasterReport.class);
    MasterReport report = (MasterReport) directly.getResource();
    report.setCompatibilityLevel(null);

    Band element = (Band) report.getReportHeader().getElement(2);
    element.setName("Tester");
    element.getElement(0).setName("m1");

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand(report, report.getReportHeader());
    ModelPrinter.INSTANCE.print(logicalPageBox);
    RenderNode m1 = MatchFactory.findElementByName(logicalPageBox, "m1");
    assertEquals(StrictGeomUtility.toInternalValue(234), m1.getX());
  }

  public void testGoldRun6 () throws Exception
  {
    File file = GoldTestBase.locateGoldenSampleReport("Prd-3514.prpt");
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly(file, MasterReport.class);
    MasterReport report = (MasterReport) directly.getResource();

//    report.setCompatibilityLevel(ClassicEngineBoot.computeVersionId(3, 8, 0));

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(report, 0);
    ModelPrinter.INSTANCE.print(logicalPageBox);
  }


  public void testGoldRun7 () throws Exception
  {
    File file = GoldTestBase.locateGoldenSampleReport("pre111.xml");
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly(file, MasterReport.class);
    MasterReport report = (MasterReport) directly.getResource();

    report.setCompatibilityLevel(null);

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(report, 2);
    //ModelPrinter.INSTANCE.print(logicalPageBox);
    RenderBox autoChild = (RenderBox) logicalPageBox.getHeaderArea().getFirstChild();
    RenderBox canvasChild = (RenderBox) autoChild.getFirstChild();
    RenderNode line1 = canvasChild.getFirstChild();
    RenderNode line2 = line1.getNext();

    assertEquals(0, line1.getX());
    assertEquals(0, line1.getY());
    assertEquals(StrictGeomUtility.toInternalValue(504), line1.getWidth());
    assertEquals(0, line1.getHeight());

    assertEquals(0, line2.getX());
    assertEquals(StrictGeomUtility.toInternalValue(3), line2.getY());
    assertEquals(StrictGeomUtility.toInternalValue(504), line2.getWidth());
    assertEquals(0, line2.getHeight());

    DebugReportRunner.createXmlFlow(report);

  }


  public void testGoldRun8 () throws Exception
  {
    File file = GoldTestBase.locateGoldenSampleReport("OrderDetailReport.xml");
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly(file, MasterReport.class);
    MasterReport report = (MasterReport) directly.getResource();
    report.setAttribute(AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMAPTIBILITY_LEVEL, null);
    report.getReportHeader().getElement(3).setName("image");

    LogicalPageBox pageBox = DebugReportRunner.layoutPage(report, 0);
    ModelPrinter.INSTANCE.print(pageBox);
    RenderNode img = MatchFactory.findElementByName(pageBox, "image");
    assertEquals(StrictGeomUtility.toInternalValue(0), img.getX());
    assertEquals(StrictGeomUtility.toInternalValue(234), img.getWidth());
  }

}
