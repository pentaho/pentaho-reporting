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


package org.pentaho.reporting.engine.classic.demo.util;

import java.net.URL;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * The AbstractXmlDemoHandler helps to simplify demo reports which read their report definition from an XML file.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractXmlDemoHandler extends AbstractDemoHandler
    implements XmlDemoHandler
{
  public AbstractXmlDemoHandler()
  {
  }

  protected MasterReport parseReport() throws ReportDefinitionException
  {
    final URL in = getReportDefinitionSource();
    if (in == null)
    {
      throw new ReportDefinitionException("ReportDefinition Source is invalid");
    }

    try
    {
      ResourceManager manager = new ResourceManager();
      Resource res = manager.createDirectly(in, MasterReport.class);
      return (MasterReport) res.getResource();
    }
    catch (Exception e)
    {
      throw new ReportDefinitionException("Parsing failed", e);
    }
  }

  public PreviewHandler getPreviewHandler()
  {
    return new DefaultPreviewHandler(this);
  }
}
