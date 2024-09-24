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

package org.pentaho.reporting.engine.classic.core.parameters;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * The parameter context is provided by the reporting engine to connect the parameter system with the data-sources and
 * user-defined parameters..
 *
 * @author Thomas Morgner
 */
public interface ParameterContext {
  /**
   * the document metadata of the report. Can be null, if the report does not have a bundle associated or if this
   * context is not part of a report-processing.
   *
   * @return
   */
  public DocumentMetaData getDocumentMetaData();

  public ReportEnvironment getReportEnvironment();

  public DataRow getParameterData();

  public DataFactory getDataFactory();

  public ResourceBundleFactory getResourceBundleFactory();

  public ResourceKey getContentBase();

  public ResourceManager getResourceManager();

  public Configuration getConfiguration();

  public PerformanceMonitorContext getPerformanceMonitorContext();

  public void close() throws ReportDataFactoryException;
}
