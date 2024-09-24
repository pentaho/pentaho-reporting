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
