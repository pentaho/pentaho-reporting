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

package org.pentaho.reporting.engine.classic.core.modules.parser.extwriter;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DataSourceCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements.ElementFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.StyleKeyFactoryCollector;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.TemplateCollector;

/**
 * Creation-Date: Jan 22, 2007, 3:06:53 PM
 *
 * @author Thomas Morgner
 */
public class ReportWriterContext {
  private ReportWriterContext parent;
  private AbstractReportDefinition reportDefinition;

  protected ReportWriterContext( final AbstractReportDefinition reportDefinition ) {
    if ( reportDefinition == null ) {
      throw new NullPointerException( "Report is null" );
    }
    this.reportDefinition = reportDefinition;
  }

  public ReportWriterContext( final AbstractReportDefinition reportDefinition, final ReportWriterContext parent ) {
    if ( reportDefinition == null ) {
      throw new NullPointerException( "Report is null" );
    }
    if ( parent == null ) {
      throw new NullPointerException( "Parent is null" );
    }
    this.reportDefinition = reportDefinition;
    this.parent = parent;
  }

  public AbstractReportDefinition getReport() {
    return reportDefinition;
  }

  public ClassFactoryCollector getClassFactoryCollector() {
    return parent.getClassFactoryCollector();
  }

  public ElementFactoryCollector getElementFactoryCollector() {
    return parent.getElementFactoryCollector();
  }

  public StyleKeyFactoryCollector getStyleKeyFactoryCollector() {
    return parent.getStyleKeyFactoryCollector();
  }

  public TemplateCollector getTemplateCollector() {
    return parent.getTemplateCollector();
  }

  public DataSourceCollector getDataSourceCollector() {
    return parent.getDataSourceCollector();
  }

  public boolean hasParent() {
    return parent != null;
  }
}
