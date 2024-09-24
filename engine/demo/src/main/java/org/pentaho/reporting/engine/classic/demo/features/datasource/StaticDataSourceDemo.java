/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.features.datasource;

import java.net.URL;
import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.NamedStaticDataFactory;
import org.pentaho.reporting.engine.classic.demo.util.AbstractXmlDemoHandler;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;

/**
 * Creation-Date: 20.11.2007, 12:58:57
 *
 * @author Thomas Morgner
 */
public class StaticDataSourceDemo extends AbstractXmlDemoHandler
{

  public String getDemoName()
  {
    return null;
  }

  public MasterReport createReport() throws ReportDefinitionException
  {
    return null;
  }

  public URL getDemoDescriptionSource()
  {
    return null;
  }

  public JComponent getPresentationComponent()
  {
    return null;
  }

  public URL getReportDefinitionSource()
  {
    return null;
  }

  public static void main(String[] args)
  {
    NamedStaticDataFactory nsdf = new NamedStaticDataFactory();
    nsdf.setQuery("default",
        "org.pentaho.reporting.engine.classic.demo.features.datasource.StaticDataSourceDemo#createMainQuery");
    nsdf.setQuery("sub-report",
        "org.pentaho.reporting.engine.classic.demo.features.datasource.StaticDataSourceDemo#createSubReportQuery(parameter)");

  }
}
