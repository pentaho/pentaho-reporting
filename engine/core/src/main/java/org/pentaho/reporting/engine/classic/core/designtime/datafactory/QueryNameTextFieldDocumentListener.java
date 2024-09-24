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

package org.pentaho.reporting.engine.classic.core.designtime.datafactory;

import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;

import javax.swing.event.DocumentEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public abstract class QueryNameTextFieldDocumentListener<T> extends DocumentChangeHandler implements ListDataListener {
  private boolean inUpdate;
  private NamedQueryModel<T> dialogModel;

  protected QueryNameTextFieldDocumentListener( final NamedQueryModel<T> dialogModel ) {
    this.dialogModel = dialogModel;
    this.dialogModel.getQueries().addListDataListener( this );
  }

  public void intervalAdded( final ListDataEvent e ) {
  }

  public void intervalRemoved( final ListDataEvent e ) {
  }

  public void contentsChanged( final ListDataEvent e ) {
    if ( inUpdate ) {
      return;
    }
    if ( e.getIndex0() != -1 ) {
      return;
    }

    try {
      inUpdate = true;

      final DataSetQuery<T> selectedQuery = dialogModel.getQueries().getSelectedQuery();
      if ( selectedQuery == null ) {
        setEditorQuery( null );
        return;
      }

      setEditorQuery( selectedQuery );
    } finally {
      inUpdate = false;
    }
  }

  protected abstract void setEditorQuery( DataSetQuery<T> query );

  protected void handleChange( final DocumentEvent e ) {
    if ( inUpdate ) {
      return;
    }
    final DataSetQuery<T> item = dialogModel.getQueries().getSelectedQuery();
    if ( item == null ) {
      return;
    }

    try {
      inUpdate = true;
      final Document document = e.getDocument();
      final String queryName = document.getText( 0, document.getLength() );
      item.setQueryName( queryName );
      dialogModel.getQueries().fireItemChanged( item );
    } catch ( BadLocationException e1 ) {
      e1.printStackTrace();
    } finally {
      inUpdate = false;
    }

  }
}
