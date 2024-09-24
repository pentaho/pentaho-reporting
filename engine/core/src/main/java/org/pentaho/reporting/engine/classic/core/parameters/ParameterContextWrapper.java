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

public class ParameterContextWrapper implements ParameterContext {
  private DataRow parameters;
  private ParameterContext backend;

  public ParameterContextWrapper( final ParameterContext backend, final DataRow parameters ) {
    this.backend = backend;
    this.parameters = parameters;
  }

  public DocumentMetaData getDocumentMetaData() {
    return backend.getDocumentMetaData();
  }

  public ReportEnvironment getReportEnvironment() {
    return backend.getReportEnvironment();
  }

  public DataRow getParameterData() {
    return parameters;
  }

  public DataFactory getDataFactory() {
    return backend.getDataFactory();
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return backend.getResourceBundleFactory();
  }

  public ResourceKey getContentBase() {
    return backend.getContentBase();
  }

  public ResourceManager getResourceManager() {
    return backend.getResourceManager();
  }

  public Configuration getConfiguration() {
    return backend.getConfiguration();
  }

  public void close() throws ReportDataFactoryException {
    backend.close();
  }

  public PerformanceMonitorContext getPerformanceMonitorContext() {
    return backend.getPerformanceMonitorContext();
  }
}
