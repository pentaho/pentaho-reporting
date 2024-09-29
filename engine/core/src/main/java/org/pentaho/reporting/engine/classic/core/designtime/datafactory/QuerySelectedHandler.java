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

import javax.swing.JList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class QuerySelectedHandler<T> implements ListSelectionListener, ListDataListener {
  private NamedQueryModel<T> queries;
  private JList queryNameList;

  public QuerySelectedHandler( final NamedQueryModel<T> queries, final JList queryNameList ) {
    if ( queryNameList == null ) {
      throw new NullPointerException();
    }
    if ( queries == null ) {
      throw new NullPointerException();
    }
    this.queries = queries;
    this.queryNameList = queryNameList;
    this.queryNameList.addListSelectionListener( this );
    this.queries.getQueries().addListDataListener( this );
  }

  public void valueChanged( final ListSelectionEvent e ) {
    final DataSetQuery<T> selectedValue = (DataSetQuery<T>) queryNameList.getSelectedValue();
    queries.setSelectedDataSetQuery( selectedValue );
  }

  public void intervalAdded( final ListDataEvent e ) {

  }

  public void intervalRemoved( final ListDataEvent e ) {

  }

  public void contentsChanged( final ListDataEvent e ) {
    final Object selectedValueInList = queryNameList.getSelectedValue();
    final Object selectedItem = queries.getQueries().getSelectedItem();
    if ( selectedItem == selectedValueInList ) {
      return;
    }

    if ( selectedItem == null ) {
      queryNameList.clearSelection();
    } else {
      queryNameList.setSelectedValue( selectedItem, true );
    }
  }
}
