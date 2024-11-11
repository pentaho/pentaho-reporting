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


package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.beans.PropertyChangeListener;
import javax.swing.DefaultComboBoxModel;

import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinitionManager;

public interface DataSourceDialogModel
{
  public JdbcConnectionDefinitionManager getConnectionDefinitionManager();
  
  public DefaultComboBoxModel getConnections();

  public void addPropertyChangeListener(final PropertyChangeListener changeListener);

  public void removePropertyChangeListener(final PropertyChangeListener listener);

  public boolean isConnectionSelected();

  public void removeConnection(final JdbcConnectionDefinition connection);

  public void addConnection(final JdbcConnectionDefinition connectionDefinition);

  public void editConnection(JdbcConnectionDefinition oldConnection, JdbcConnectionDefinition newConnection);

  public String getJdbcUserField();
  public String getJdbcPasswordField();

  public void setJdbcUserField(String userField);
  public void setJdbcPasswordField(String passwordField);
}
