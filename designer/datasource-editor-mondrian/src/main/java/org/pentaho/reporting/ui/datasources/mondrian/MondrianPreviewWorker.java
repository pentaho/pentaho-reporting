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


package org.pentaho.reporting.ui.datasources.mondrian;

import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.states.QueryDataRowWrapper;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelEvent;
import org.pentaho.reporting.libraries.designtime.swing.background.PreviewWorker;

import javax.swing.table.TableModel;

public class MondrianPreviewWorker implements PreviewWorker {
  private AbstractMDXDataFactory dataFactory;
  private TableModel resultTableModel;
  private ReportDataFactoryException exception;
  private String query;
  private int queryTimeout;
  private int queryLimit;

  public MondrianPreviewWorker( final AbstractMDXDataFactory dataFactory,
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
      resultTableModel = dataFactory.queryData
        ( query, new QueryDataRowWrapper( new ParameterDataRow(), queryLimit, queryTimeout ) );
    } catch ( ReportDataFactoryException e ) {
      exception = e;
    } finally {
      dataFactory.close();
    }
  }
}
