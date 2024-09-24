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
