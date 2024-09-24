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
