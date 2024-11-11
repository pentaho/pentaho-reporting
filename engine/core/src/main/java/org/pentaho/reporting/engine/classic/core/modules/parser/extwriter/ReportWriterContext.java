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
