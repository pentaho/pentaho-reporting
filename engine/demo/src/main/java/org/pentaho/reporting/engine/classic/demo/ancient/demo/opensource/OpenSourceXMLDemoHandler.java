/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.demo.ancient.demo.opensource;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;

/**
 * A simple JFreeReport demonstration.  The generated report lists some free and open source software projects for the
 * Java programming language.
 *
 * @author David Gilbert
 */
public class OpenSourceXMLDemoHandler extends AbstractXmlDemoHandler
{
  /**
   * The data for the report.
   */
  private TableModel data;

  /**
   * Constructs the demo application.
   */
  public OpenSourceXMLDemoHandler()
  {
    this.data = new OpenSourceProjects();
  }

  public String getDemoName()
  {
    return "Open Source Demo (XML)";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));

    final URL imageURL = ObjectUtilities.getResourceRelative
        ("gorilla.jpg", OpenSourceXMLDemoHandler.class);
    final Image image = Toolkit.getDefaultToolkit().createImage(imageURL);
    final WaitingImageObserver obs = new WaitingImageObserver(image);
    obs.waitImageLoaded();
    report.getParameterValues().put("logo", image);
    return report;
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("opensource-xml.html", OpenSourceXMLDemoHandler.class);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("opensource.xml", OpenSourceXMLDemoHandler.class);
  }

  public static void main(final String[] args)
      throws ReportDefinitionException, ReportProcessingException, IOException
  {
    ClassicEngineBoot.getInstance().start();
    final OpenSourceXMLDemoHandler handler = new OpenSourceXMLDemoHandler();
//    PdfReportUtil.createPDF(handler.createReport(), "/tmp/report.pdf");

    HtmlReportUtil.createDirectoryHTML(handler.createReport(), "/tmp/report.html");


  }
}
