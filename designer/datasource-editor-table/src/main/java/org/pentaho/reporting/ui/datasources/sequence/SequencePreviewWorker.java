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


package org.pentaho.reporting.ui.datasources.sequence;

import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataFactoryEditorSupport;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.Sequence;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDataFactory;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelEvent;
import org.pentaho.reporting.libraries.designtime.swing.background.PreviewWorker;

import javax.swing.table.TableModel;

public class SequencePreviewWorker implements PreviewWorker {
  private SequenceDataFactory dataFactory;
  private TableModel resultTableModel;
  private ReportDataFactoryException exception;

  public SequencePreviewWorker( final Sequence query,
                                final DesignTimeContext context ) throws ReportProcessingException {
    this.dataFactory = new SequenceDataFactory();
    DataFactoryEditorSupport.configureDataFactoryForPreview( dataFactory, context );
    this.dataFactory.addSequence( "preview", query );
  }

  public ReportDataFactoryException getException() {
    return exception;
  }

  public TableModel getResultTableModel() {
    return resultTableModel;
  }

  public void close() {
    dataFactory.close();
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
      resultTableModel = dataFactory.queryData( "preview", new ParameterDataRow() );
    } catch ( ReportDataFactoryException e ) {
      exception = e;
    } finally {
      dataFactory.close();
    }
  }
}
