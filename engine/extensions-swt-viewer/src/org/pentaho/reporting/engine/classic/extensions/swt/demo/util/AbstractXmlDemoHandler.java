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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.swt.demo.util;

import java.net.URL;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.demo.util.ReportDefinitionException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.Resource;

/**
 * The AbstractXmlDemoHandler helps to simplify demo reports which read their
 * report definition from an XML file.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractXmlDemoHandler extends AbstractDemoHandler
        implements XmlDemoHandler
{
  protected AbstractXmlDemoHandler()
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
      final ResourceManager manager = new ResourceManager();
      manager.registerDefaults();
      final Resource res = manager.createDirectly(in, MasterReport.class);
      return (MasterReport) res.getResource();
    }
    catch(Exception e)
    {
      throw new ReportDefinitionException("Parsing failed", e);
    }
  }
}
