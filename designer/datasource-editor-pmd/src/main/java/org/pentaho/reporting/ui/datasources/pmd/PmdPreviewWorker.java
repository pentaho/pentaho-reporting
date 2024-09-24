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

package org.pentaho.reporting.ui.datasources.pmd;

import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.states.LengthLimitingTableModel;
import org.pentaho.reporting.engine.classic.core.states.QueryDataRowWrapper;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactoryModule;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelEvent;
import org.pentaho.reporting.libraries.designtime.swing.background.PreviewWorker;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class PmdPreviewWorker implements PreviewWorker {
  private static class PreviewTableModel implements CloseableTableModel, MetaTableModel {
    private MetaTableModel metaTableModel;
    private DefaultDataAttributeContext dataAttributeContext;

    private PreviewTableModel( final MetaTableModel metaTableModel ) {
      this.metaTableModel = metaTableModel;
      dataAttributeContext = new DefaultDataAttributeContext();
    }

    /**
     * If this model has disposeable resources assigned, close them or dispose them.
     */
    public void close() {
      if ( metaTableModel instanceof CloseableTableModel ) {
        final CloseableTableModel ctm = (CloseableTableModel) metaTableModel;
        ctm.close();
      }
    }

    public DataAttributes getCellDataAttributes( final int row, final int column ) {
      return metaTableModel.getCellDataAttributes( row, column );
    }

    public boolean isCellDataAttributesSupported() {
      return metaTableModel.isCellDataAttributesSupported();
    }

    public DataAttributes getColumnAttributes( final int column ) {
      return metaTableModel.getColumnAttributes( column );
    }

    public DataAttributes getTableAttributes() {
      return metaTableModel.getTableAttributes();
    }

    public int getRowCount() {
      return metaTableModel.getRowCount();
    }

    public int getColumnCount() {
      return metaTableModel.getColumnCount();
    }

    public String getColumnName( final int columnIndex ) {
      final DataAttributes columnAttributes = getColumnAttributes( columnIndex );
      final String friendlyName = (String) columnAttributes.getMetaAttribute
        ( PmdDataFactoryModule.META_DOMAIN, MetaAttributeNames.Core.NAME,
          String.class, dataAttributeContext );
      if ( friendlyName != null ) {
        return friendlyName;
      }
      return metaTableModel.getColumnName( columnIndex );
    }

    public Class getColumnClass( final int columnIndex ) {
      return metaTableModel.getColumnClass( columnIndex );
    }

    public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
      return metaTableModel.isCellEditable( rowIndex, columnIndex );
    }

    public Object getValueAt( final int rowIndex, final int columnIndex ) {
      return metaTableModel.getValueAt( rowIndex, columnIndex );
    }

    public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
      metaTableModel.setValueAt( aValue, rowIndex, columnIndex );
    }

    public void addTableModelListener( final TableModelListener l ) {
      metaTableModel.addTableModelListener( l );
    }

    public void removeTableModelListener( final TableModelListener l ) {
      metaTableModel.removeTableModelListener( l );
    }
  }

  private PmdDataFactory dataFactory;
  private PreviewTableModel resultTableModel;
  private ReportDataFactoryException exception;
  private String query;
  private int queryTimeout;
  private int queryLimit;

  public PmdPreviewWorker( final PmdDataFactory dataFactory,
                           final String query,
                           final int queryTimeout,
                           final int queryLimit ) {
    this.queryTimeout = queryTimeout;
    this.queryLimit = queryLimit;
    if ( dataFactory == null ) {
      throw new NullPointerException();
    }
    this.query = query;
    this.dataFactory = dataFactory;
  }

  public ReportDataFactoryException getException() {
    return exception;
  }

  public TableModel getResultTableModel() {
    return resultTableModel;
  }

  public void close() {
    if ( resultTableModel != null ) {
      resultTableModel.close();
      resultTableModel = null;
    }

    if ( dataFactory != null ) {
      dataFactory.close();
      dataFactory = null;
    }

  }

  /**
   * Requests that the thread stop processing as soon as possible.
   */
  public void cancelProcessing( final CancelEvent event ) {
    dataFactory.cancelRunningQuery();
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread causes
   * the object's <code>run</code> method to be called in that separately executing thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  public void run() {
    try {
      final TableModel tableModel = dataFactory.queryData
        ( query, new QueryDataRowWrapper( new ParameterDataRow(), queryLimit, queryTimeout ) );
      if ( queryLimit > 0 ) {
        resultTableModel = new PreviewTableModel( new LengthLimitingTableModel( tableModel, queryLimit ) );
      } else {
        resultTableModel = new PreviewTableModel( (MetaTableModel) tableModel );
      }
    } catch ( ReportDataFactoryException e ) {
      exception = e;
    }
  }
}
