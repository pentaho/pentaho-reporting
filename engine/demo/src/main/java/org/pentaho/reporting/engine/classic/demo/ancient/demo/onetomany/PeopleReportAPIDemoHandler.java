/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.demo.ancient.demo.onetomany;

import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A demo handler that shows how to define the one-to-many reports using the API.
 *
 * @author Thomas Morgner
 */
public class PeopleReportAPIDemoHandler extends AbstractDemoHandler
{
  private PeopleReportTableModel tableModel;

  public PeopleReportAPIDemoHandler()
  {
    tableModel = new PeopleReportTableModel();
  }

  public String getDemoName()
  {
    return "One-To-Many-Elements Reports Demo (API-Version)";
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    final PeopleReportDefinition reportCreator = new PeopleReportDefinition();
    final MasterReport report = reportCreator.getReport();
    report.setDataFactory(new TableDataFactory
        ("default", tableModel));
    return report;
  }

  public URL getDemoDescriptionSource()
  {
    return ObjectUtilities.getResourceRelative
        ("people-api.html", PeopleReportAPIDemoHandler.class);
  }

  public JComponent getPresentationComponent()
  {
    return createDefaultTable(tableModel);
  }

}
