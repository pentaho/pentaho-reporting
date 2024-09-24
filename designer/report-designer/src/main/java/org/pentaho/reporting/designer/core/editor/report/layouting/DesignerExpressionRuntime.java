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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class DesignerExpressionRuntime implements ExpressionRuntime {
  private DataRow dataRow;
  private DataSchema dataSchema;
  private TableModel tableModel;
  private DesignerProcessingContext processingContext;
  private MasterReport report;

  public DesignerExpressionRuntime( final DataRow dataRow,
                                    final DataSchema dataSchema,
                                    final MasterReport report ) {
    this.report = report;
    if ( report == null ) {
      throw new NullPointerException();
    }

    this.dataRow = dataRow;
    this.dataSchema = dataSchema;
    this.tableModel = new DefaultTableModel();
    try {
      this.processingContext = new DesignerProcessingContext( report );
    } catch ( ReportProcessingException e ) {
      this.processingContext = new DesignerProcessingContext();
    }
  }

  public DataFactory getDataFactory() {
    return report.getDataFactory();
  }

  public ResourceKey getContentBase() {
    return processingContext.getContentBase();
  }

  public void setContentBase( final ResourceKey contentBase ) {
    processingContext.setContentBase( contentBase );
  }

  public void setDataSchema( final DataSchema dataSchema ) {
    if ( dataSchema == null ) {
      throw new NullPointerException();
    }
    this.dataSchema = dataSchema;
  }

  public DataRow getDataRow() {
    return dataRow;
  }

  public DataSchema getDataSchema() {
    return dataSchema;
  }

  public Configuration getConfiguration() {
    return processingContext.getConfiguration();
  }

  public ResourceBundleFactory getResourceBundleFactory() {
    return processingContext.getResourceBundleFactory();
  }

  public TableModel getData() {
    return tableModel;
  }

  public int getCurrentRow() {
    return 0;
  }

  public int getCurrentDataItem() {
    return 0;
  }

  public String getExportDescriptor() {
    return "pageable/report-designer"; // NON-NLS
  }

  public ProcessingContext getProcessingContext() {
    return processingContext;
  }

  public int getCurrentGroup() {
    return 0;
  }

  public int getGroupStartRow( final String groupName ) {
    return 0;
  }

  public int getGroupStartRow( final int groupIndex ) {
    return 0;
  }

  public boolean isStructuralComplexReport() {
    return false;
  }

  public boolean isCrosstabActive() {
    return false;
  }
}
