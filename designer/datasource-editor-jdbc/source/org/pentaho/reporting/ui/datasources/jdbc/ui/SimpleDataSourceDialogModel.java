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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.database.model.IDatabaseType;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DataSourceMgmtService;
import org.pentaho.reporting.libraries.base.boot.ObjectFactory;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinitionManager;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JndiConnectionDefinition;


public class SimpleDataSourceDialogModel implements DataSourceDialogModel
{
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
      setConnectionSelected(getConnections().getSelectedItem() != null);
      if (getConnections().getSelectedItem() == null)
      {
        setPreviewPossible(false);
        return;
      }
      setPreviewPossible(true);
    }
  }

  private PropertyChangeSupport propertyChangeSupport;
  private DefaultComboBoxModel<JdbcConnectionDefinition> connections;
  private boolean previewPossible;
  private boolean connectionSelected;
  private JdbcConnectionDefinitionManager connectionDefinitionManager;
  private String jdbcUserField;
  private String jdbcPasswordField;

  public SimpleDataSourceDialogModel()
  {
    this(new JdbcConnectionDefinitionManager());
  }

  public SimpleDataSourceDialogModel(final JdbcConnectionDefinitionManager connectionDefinitionManager)
  {
    this.connectionDefinitionManager = connectionDefinitionManager;
    propertyChangeSupport = new PropertyChangeSupport(this);
    connections = new DefaultComboBoxModel<JdbcConnectionDefinition>();
    connections.addListDataListener(new PreviewPossibleUpdateHandler());
  }

  public void clear()
  {
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

    readSharedConnections();
    connections.setSelectedItem(null);
  }

  private void readSharedConnections()
  {
    final ObjectFactory objectFactory = ClassicEngineBoot.getInstance().getObjectFactory();
    try
    {
      final DataSourceMgmtService mgmtService = objectFactory.get(DataSourceMgmtService.class);
      final List<IDatabaseConnection> datasources = mgmtService.getDatasources();
      for (int i = 0; i < datasources.size(); i++)
      {
        final IDatabaseConnection connection = datasources.get(i);
        final IDatabaseType databaseType = connection.getDatabaseType();
        final String shortName;
        if (databaseType == null)
        {
          shortName = "GENERIC";
        }
        else
        {
          shortName = databaseType.getShortName();
        }

        connections.addElement(new JndiConnectionDefinition
            (connection.getName(), connection.getName(), shortName, null, null, true));
      }
    }
    catch (Exception e)
    {
      // ignore
      e.printStackTrace();
    }
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

  public void removeConnection(final JdbcConnectionDefinition definition)
  {
    connections.removeElement(definition);
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
    final boolean oldConnectionSelected = this.connectionSelected;
    this.connectionSelected = connectionSelected;
    propertyChangeSupport.firePropertyChange("connectionSelected", oldConnectionSelected, connectionSelected);
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
}
