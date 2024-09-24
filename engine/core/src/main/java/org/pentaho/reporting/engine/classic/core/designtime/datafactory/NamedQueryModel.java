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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.pentaho.reporting.libraries.base.util.StringUtils;

public abstract class NamedQueryModel<T> {
  public static final String QUERY_SELECTED = "querySelected";
  public static final String PREVIEW_POSSIBLE = "previewPossible";

  private class PreviewPossibleUpdateHandler implements ListDataListener {
    private PreviewPossibleUpdateHandler() {
    }

    public void intervalAdded( final ListDataEvent e ) {
      contentsChanged( e );
    }

    public void intervalRemoved( final ListDataEvent e ) {
      contentsChanged( e );
    }

    public void contentsChanged( final ListDataEvent e ) {
      validateState();
    }

  }

  private boolean previewPossible;
  private boolean querySelected;
  private DataSetComboBoxModel<T> queries;
  private PropertyChangeSupport propertyChangeSupport;

  public NamedQueryModel() {
    propertyChangeSupport = new PropertyChangeSupport( this );
    queries = new DataSetComboBoxModel<T>();
    queries.addListDataListener( new PreviewPossibleUpdateHandler() );
  }

  public boolean isQuerySelected() {
    return querySelected;
  }

  protected void setQuerySelected( final boolean querySelected ) {
    this.querySelected = querySelected;
    propertyChangeSupport.firePropertyChange( QUERY_SELECTED, !querySelected, querySelected );
  }

  public void addQuery( final String name, final T value ) {
    queries.addElement( new DataSetQuery<T>( name, value ) );
  }

  public void addQuery( final String name, final T value, final String language, final String script ) {
    queries.addElement( new DataSetQuery<T>( name, value, language, script ) );
  }

  public boolean createQuery() {
    final String queryNamePattern = Messages.getInstance().getString( "NamedQueryModel.Query" );
    final String queryName = generateQueryName( queryNamePattern );
    final T defaultObject = createDefaultObject();
    if ( defaultObject == null ) {
      return false;
    }

    final DataSetQuery<T> query = new DataSetQuery<T>( queryName, defaultObject );
    queries.addElement( query );
    setSelectedDataSetQuery( query );
    return true;
  }

  protected abstract T createDefaultObject();

  public void setSelectedDataSetQuery( final DataSetQuery<T> dataSetQuery ) {
    queries.setSelectedItem( dataSetQuery );
  }

  public DataSetQuery<T> getSelectedDataSetQuery() {
    return queries.getSelectedQuery();
  }

  public void setSelectedQuery( final String selectedQueryName ) {
    if ( selectedQueryName == null ) {
      setSelectedDataSetQuery( null );
      return;
    }

    for ( int i = 0; i < queries.getSize(); i += 1 ) {
      final DataSetQuery<T> q = (DataSetQuery<T>) queries.getElementAt( i );
      if ( selectedQueryName.equals( q.getQueryName() ) ) {
        setSelectedDataSetQuery( q );
        return;
      }
    }
  }

  public String generateQueryName( final String queryName ) {
    final DataSetComboBoxModel queries = getQueries();
    for ( int i = 1; i < 1000; ++i ) {
      final String newQuery = queryName + " " + i;
      if ( queries.getIndexForQuery( newQuery ) == -1 ) {
        return newQuery;
      }
    }
    return queryName;
  }

  protected void validateState() {
    final DefaultComboBoxModel queries = getQueries();
    setQuerySelected( queries.getSelectedItem() != null );

    if ( queries.getSelectedItem() == null ) {
      setPreviewPossible( false );
      return;
    }

    final DataSetQuery o = (DataSetQuery) queries.getSelectedItem();
    if ( o == null || StringUtils.isEmpty( o.getQueryName() ) ) {
      setPreviewPossible( false );
      return;
    }

    setPreviewPossible( true );
  }

  public void clear() {
    queries.removeAllElements();

    setPreviewPossible( false );
  }

  public boolean isPreviewPossible() {
    return previewPossible;
  }

  public void setPreviewPossible( final boolean previewPossible ) {
    final boolean oldPreviewPossible = this.previewPossible;
    this.previewPossible = previewPossible;
    propertyChangeSupport.firePropertyChange( PREVIEW_POSSIBLE, oldPreviewPossible, previewPossible );
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }

  public void addPropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( propertyName, listener );
  }

  public void removePropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( propertyName, listener );
  }

  public DataSetComboBoxModel<T> getQueries() {
    return queries;
  }
}
