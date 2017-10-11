/*
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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataSetComboBoxModel;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DataSetQuery;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.ui.datasources.jdbc.Messages;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinitionManager;

public class NamedDataSourceDialogModel implements DataSourceDialogModel
{

  public static final String QUERY_SELECTED = "querySelected";
  public static final String CONNECTION_SELECTED = "connectionSelected";

  private class PreviewPossibleUpdateHandler implements ListDataListener
  {
    private PreviewPossibleUpdateHandler()
    {
    }

    public void intervalAdded(final ListDataEvent e)
    {
      contentsChanged(e);
    }

    public void intervalRemoved(final ListDataEvent e)
    {
      contentsChanged(e);
    }

    public void contentsChanged(final ListDataEvent e)
    {
      final DefaultComboBoxModel connections = getConnections();
      final DataSetComboBoxModel<String> queries = getQueries();
      setConnectionSelected(connections.getSelectedItem() != null);
      setQuerySelected(queries.getSelectedItem() != null);

      if (connections.getSelectedItem() == null)
      {
        setPreviewPossible(false);
        return;
      }

      if (queries.getSelectedItem() == null)
      {
        setPreviewPossible(false);
        return;
      }

      final DataSetQuery o = (DataSetQuery) queries.getSelectedItem();
      if (o == null || StringUtils.isEmpty(o.getQueryName()))
      {
        setPreviewPossible(false);
        return;
      }

      setPreviewPossible(true);
    }
  }

  private PropertyChangeSupport propertyChangeSupport;
  private DefaultComboBoxModel connections;
  private DataSetComboBoxModel<String> queries;
  private boolean previewPossible;
  private boolean connectionSelected;
  private boolean querySelected;
  private JdbcConnectionDefinitionManager connectionDefinitionManager;
  private String jdbcUserField;
  private String jdbcPasswordField;
  private JTextField schemaFileNameField;

  public NamedDataSourceDialogModel()
  {
    this(new JdbcConnectionDefinitionManager());
  }

  public NamedDataSourceDialogModel(final JdbcConnectionDefinitionManager connectionDefinitionManager)
  {
    this.connectionDefinitionManager = connectionDefinitionManager;
    propertyChangeSupport = new PropertyChangeSupport(this);
    connections = new DefaultComboBoxModel();
    connections.addListDataListener(new PreviewPossibleUpdateHandler());
    queries = new DataSetComboBoxModel<String>();
    queries.addListDataListener(new PreviewPossibleUpdateHandler());
  }

  public void clear()
  {
    queries.removeAllElements();
    connections.removeAllElements();
    setJdbcPasswordField(null);
    setJdbcUserField(null);

    setPreviewPossible(false);

    final JdbcConnectionDefinition[] jdbcConnectionDefinitions = connectionDefinitionManager.getSources();
    for (int i = 0; i < jdbcConnectionDefinitions.length; i++)
    {
      final JdbcConnectionDefinition definition = jdbcConnectionDefinitions[i];
      connections.addElement(definition);
    }
    connections.setSelectedItem(null);
  }

  public void addPropertyChangeListener(final PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(final PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
  }

  public DataSetComboBoxModel<String> getQueries()
  {
    return queries;
  }

  public DefaultComboBoxModel getConnections()
  {
    return connections;
  }

  public void addConnection(final JdbcConnectionDefinition definition)
  {
    if (connections.getIndexOf(definition) == -1)
    {
      connections.addElement(definition);
    }
  }

  public void removeConnection(final JdbcConnectionDefinition definition)
  {
    connections.removeElement(definition);
  }

  public void editConnection(final JdbcConnectionDefinition oldConnection, final JdbcConnectionDefinition newConnection)
  {
    if (oldConnection == null)
    {
      if (newConnection != null)
      {
        addConnection(newConnection);
      }
      return;
    }

    if (newConnection == null)
    {
      removeConnection(oldConnection);
      return;
    }

    final int index = connections.getIndexOf(oldConnection);
    if (index == -1)
    {
      connections.addElement(newConnection);
    }
    else
    {
      connections.insertElementAt(newConnection, index);
    }
    connections.removeElement(oldConnection);
  }

  public boolean isPreviewPossible()
  {
    return previewPossible;
  }

  public void setPreviewPossible(final boolean previewPossible)
  {
    final boolean oldPreviewPossible = this.previewPossible;
    this.previewPossible = previewPossible;
    propertyChangeSupport.firePropertyChange("previewPossible", oldPreviewPossible, previewPossible);
  }

  public boolean isConnectionSelected()
  {
    return connectionSelected;
  }

  public void setConnectionSelected(final boolean connectionSelected)
  {
    this.connectionSelected = connectionSelected;
    propertyChangeSupport.firePropertyChange(CONNECTION_SELECTED, !connectionSelected, connectionSelected);
  }

  public boolean isQuerySelected()
  {
    return querySelected;
  }

  public void setQuerySelected(final boolean querySelected)
  {
    this.querySelected = querySelected;
    propertyChangeSupport.firePropertyChange(QUERY_SELECTED, !querySelected, querySelected);
  }

  public void addQuery(final String queryName, final String query, final String scriptLanguage, final String script)
  {
    queries.addElement(new DataSetQuery<String>(queryName, query, scriptLanguage, script));
  }

  public JdbcConnectionDefinitionManager getConnectionDefinitionManager()
  {
    return connectionDefinitionManager;
  }

  public String getJdbcUserField()
  {
    return jdbcUserField;
  }

  public void setJdbcUserField(final String jdbcUserField)
  {
    final String oldUser = this.jdbcUserField;
    this.jdbcUserField = jdbcUserField;
    propertyChangeSupport.firePropertyChange("jdbcUserField", oldUser, jdbcUserField);
  }

  public String getJdbcPasswordField()
  {
    return jdbcPasswordField;
  }

  public void setJdbcPasswordField(final String jdbcPasswordField)
  {
    final String oldPassword = this.jdbcPasswordField;
    this.jdbcPasswordField = jdbcPasswordField;
    propertyChangeSupport.firePropertyChange("jdbcPasswordField", oldPassword, jdbcPasswordField);
  }

  public JTextField getSchemaFileNameField()
  {
    return schemaFileNameField;
  }

  public void setSchemaFileNameField(final JTextField schemaFileNameField)
  {
    this.schemaFileNameField = schemaFileNameField;
  }

  public void setSelectedQuery(final String selectedQueryName)
  {

    if(queries == null || queries.getSize() == 0)
    {
      return;
    }
    if (selectedQueryName == null)
    {
      queries.setSelectedItem(getFirstQueryName());
      setQuerySelected(getFirstQueryName() != null);
      setPreviewPossible(getFirstQueryName() != null);
      return;
    }
    
    for (int i = 0; i < queries.getSize(); i += 1)
    {
      final DataSetQuery<String> q = (DataSetQuery<String>) queries.getElementAt(i);
      if (selectedQueryName.equals(q.getQueryName()))
      {
        queries.setSelectedItem(q);
        setQuerySelected(true);
        setPreviewPossible(true);
        return;
      }
    }
  }

  public DataSetQuery getFirstQueryName()
  {
    DataSetComboBoxModel<String> dataSetQueries = getQueries();
    if(dataSetQueries != null && dataSetQueries.getSize() > 0)
    {
        return dataSetQueries.getQuery(0);
    }
    return null;
  }

  public String generateQueryName()
  {
    final String queryName = Messages.getString("JdbcDataSourceDialog.Query");
    final DataSetComboBoxModel<String> queries = getQueries();
    for (int i = 1; i < 1000; ++i)
    {
      final String newQuery = queryName + " " + i;
      if (queries.getIndexForQuery(newQuery) == -1)
      {
        return newQuery;
      }
    }
    return queryName;
  }
}
