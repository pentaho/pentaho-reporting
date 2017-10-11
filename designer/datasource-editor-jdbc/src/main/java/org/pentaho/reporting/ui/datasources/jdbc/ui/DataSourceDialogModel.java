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
