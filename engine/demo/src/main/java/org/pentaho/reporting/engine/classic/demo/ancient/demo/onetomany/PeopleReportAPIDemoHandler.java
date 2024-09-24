/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

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
