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
