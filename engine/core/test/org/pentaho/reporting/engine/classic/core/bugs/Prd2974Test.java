package org.pentaho.reporting.engine.classic.core.bugs;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.XmlPageReportUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class Prd2974Test extends TestCase
{
  public Prd2974Test()
  {
  }

  public Prd2974Test(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRunStickySub() throws Exception
  {
    final URL url = getClass().getResource("Prd-2974-2.prpt");
    assertNotNull(url);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.getPageFooter().setSticky(false);
    report.getPageFooter().addElement(createLabel());
    report.getPageFooter().setName("Master-Footer");
    report.getReportHeader().getSubReport(0).getPageFooter().setName("Subreport-Footer");
    report.getReportHeader().getSubReport(0).getPageFooter().setSticky(true);
    report.getReportHeader().getSubReport(0).getReportHeader().addElement(createLabel("ReportHeader-label"));

    report.getReportConfiguration().setConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8");
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    XmlPageReportUtil.createXml(report, out);
    final String outText = out.toString("UTF-8");

    System.out.println(outText);
    assertTrue(outText.indexOf(">Label</text>") > 0);
    assertTrue(outText.indexOf("value=\"XASDAS\"") > 0);
  }


  public void testRunStickyEverything() throws Exception
  {
    final URL url = getClass().getResource("Prd-2974-2.prpt");
    assertNotNull(url);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.getPageFooter().setSticky(true);
    report.getPageFooter().addElement(createLabel());
    report.getPageFooter().setName("Master-Footer");
    report.getReportHeader().getSubReport(0).getPageFooter().setName("Subreport-Footer");
    report.getReportHeader().getSubReport(0).getPageFooter().setSticky(true);
    report.getReportHeader().getSubReport(0).getReportHeader().addElement(createLabel("ReportHeader-label"));

    report.getReportConfiguration().setConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8");
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    XmlPageReportUtil.createXml(report, out);
    final String outText = out.toString("UTF-8");

    System.out.println(outText);
    assertTrue(outText.indexOf(">Label</text>") > 0);
    assertTrue(outText.indexOf("value=\"XASDAS\"") > 0);
  }


  public void testRunNonStickyEverything() throws Exception
  {
    final URL url = getClass().getResource("Prd-2974-2.prpt");
    assertNotNull(url);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.getPageFooter().setSticky(false);
    report.getPageFooter().addElement(createLabel());
    report.getPageFooter().setName("Master-Footer");
    report.getReportHeader().getSubReport(0).getPageFooter().setName("Subreport-Footer");
    report.getReportHeader().getSubReport(0).getPageFooter().setSticky(false);
    report.getReportHeader().getSubReport(0).getReportHeader().addElement(createLabel("ReportHeader-label"));

    report.getReportConfiguration().setConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8");
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    XmlPageReportUtil.createXml(report, out);
    final String outText = out.toString("UTF-8");

    System.out.println(outText);
    assertFalse(outText.indexOf(">Label</text>") > 0);
    assertTrue(outText.indexOf("value=\"XASDAS\"") > 0);
  }

  public void testRunStickyMasterFooter() throws Exception
  {
    final URL url = getClass().getResource("Prd-2974-2.prpt");
    assertNotNull(url);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.getPageFooter().setSticky(true);
    report.getPageFooter().addElement(createLabel());
    report.getPageFooter().setName("Master-Footer");
    report.getReportHeader().getSubReport(0).getPageFooter().setName("Subreport-Footer");
    report.getReportHeader().getSubReport(0).getPageFooter().setSticky(false);
    report.getReportHeader().getSubReport(0).getReportHeader().addElement(createLabel("ReportHeader-label"));
    report.getReportConfiguration().setConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8");
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    XmlPageReportUtil.createXml(report, out);
    final String outText = out.toString("UTF-8");
/**
 * TODO: PRD-4344 - Fix this regression
 */
 /*
    System.out.println(outText);
    assertFalse(outText.indexOf(">Label</text>") > 0);
    assertTrue(outText.indexOf("value=\"XASDAS\"") > 0);
 */
  }

  private Element createLabel()
  {
    return createLabel("XASDAS");
  }

  private Element createLabel(final String text)
  {
    final Element element = new Element();
    element.setElementType(LabelType.INSTANCE);
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(20));
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, new Float(200));
    element.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text);
    return element;
  }

  public void testTwoPageReport() throws Exception
  {
    final URL url = getClass().getResource("Prd-2974-2.prpt");
    assertNotNull(url);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    report.getPageFooter().setSticky(false);
    report.getPageFooter().addElement(createLabel("PageFooter-Label"));
    report.getPageFooter().setName("Master-Footer");
    report.getReportHeader().getSubReport(0).getPageFooter().setName("Subreport-Footer");
    report.getReportHeader().getSubReport(0).getPageFooter().setSticky(true);
    report.getReportHeader().getSubReport(0).getReportHeader().addElement(createLabel("ReportHeader-label"));

    report.getReportFooter().addElement(createLabel("ReportFooter-label"));
    report.getReportFooter().setPagebreakBeforePrint(true);

    report.getReportConfiguration().setConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.Encoding", "UTF-8");
/*
    final PreviewDialog previewDialog = new PreviewDialog(report);
    previewDialog.pack();
    previewDialog.setModal(true);
    previewDialog.setVisible(true);
*/
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    XmlPageReportUtil.createXml(report, out);
    final String outText = out.toString("UTF-8");

    System.out.println(outText);
    assertTrue(outText.indexOf(">Label</text>") > 0);
    assertTrue(outText.indexOf(">PageFooter-Label<") > 0);
    // todo: this is not a true structural test at all.
  }

}

// Fix for PRD-2709 makes totally empty subreport behave like empty bands. They do not produce empty pages anymore.
// therefore this test must be changed so that the subreport is printing at least one element.
