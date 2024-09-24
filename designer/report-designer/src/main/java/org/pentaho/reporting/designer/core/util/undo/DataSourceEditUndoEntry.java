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

package org.pentaho.reporting.designer.core.util.undo;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DataSourceEditUndoEntry implements UndoEntry {
  private int position;
  private DataFactory oldElement;
  private DataFactory newElement;

  public DataSourceEditUndoEntry( final int position,
                                  final DataFactory oldElement,
                                  final DataFactory newElement ) {
    this.position = position;
    this.oldElement = oldElement;
    this.newElement = newElement;
  }

  public void undo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    final DataFactory dataFactory = abstractReportDefinition.getDataFactory();
    if ( dataFactory instanceof CompoundDataFactory == false ) {
      throw new IllegalStateException();
    }
    final CompoundDataFactory cdf = (CompoundDataFactory) dataFactory;
    if ( newElement != null ) {
      cdf.remove( position );
      abstractReportDefinition.notifyNodeChildRemoved( newElement );
    }
    if ( oldElement != null ) {
      cdf.add( position, oldElement );
      abstractReportDefinition.notifyNodeChildAdded( oldElement );
    }
  }

  public void redo( final ReportDocumentContext renderContext ) {
    final AbstractReportDefinition abstractReportDefinition = renderContext.getReportDefinition();
    final DataFactory dataFactory = abstractReportDefinition.getDataFactory();
    if ( dataFactory instanceof CompoundDataFactory == false ) {
      throw new IllegalStateException();
    }
    final CompoundDataFactory cdf = (CompoundDataFactory) dataFactory;
    if ( oldElement != null ) {
      cdf.remove( position );
      abstractReportDefinition.notifyNodeChildRemoved( oldElement );
    }
    if ( newElement != null ) {
      cdf.add( position, newElement );
      abstractReportDefinition.notifyNodeChildAdded( newElement );
    }
  }

  public UndoEntry merge( final UndoEntry newEntry ) {
    return null;
  }

}
