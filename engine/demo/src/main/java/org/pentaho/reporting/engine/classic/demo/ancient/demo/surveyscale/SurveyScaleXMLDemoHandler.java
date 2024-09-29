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


package org.pentaho.reporting.engine.classic.demo.ancient.demo.surveyscale;

import java.net.URL;
import javax.swing.JComponent;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: 27.08.2005, 16:27:19
 *
 * @author Thomas Morgner
 */
public class SurveyScaleXMLDemoHandler extends AbstractXmlDemoHandler
{
  /**
   * The core data for the report.
   */
  private TableModel data;

  public SurveyScaleXMLDemoHandler()
  {
    data = new SurveyScaleDemoTableModel();
  }

  public String getDemoName()
  {
    return "Survey Scale Demo Report (XML)";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    MasterReport report = parseReport();
    report.setDataFactory(new TableDataFactory
        ("default", data));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("survey-xml.html", SurveyScaleXMLDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(data);
  }

  public URL getReportDefinitionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("surveyscale.xml", SurveyScaleXMLDemoHandler.class);
  }
}
