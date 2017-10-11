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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ListModel;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.ui.datasources.jdbc.connection.DriverConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JndiConnectionDefinition;

public class JdbcConnectionPanel extends ConnectionPanel
{
  private class EditSecurityAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    private EditSecurityAction()
    {
      putValue(Action.NAME, getBundleSupport().getString("ConnectionPanel.EditSecurityAction.Name"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final DesignTimeContext designTimeContext = getDesignTimeContext();
      final DataSourceDialogModel dialogModel = getDialogModel();
      if (securityDialog == null)
      {
        final Window window = LibSwingUtil.getWindowAncestor(JdbcConnectionPanel.this);
        if (window instanceof Frame)
        {
          securityDialog = new JdbcSecurityDialog((Frame) window, designTimeContext);
        }
        else if (window instanceof Dialog)
        {
          securityDialog = new JdbcSecurityDialog((Dialog) window, designTimeContext);
        }
        else
        {
          securityDialog = new JdbcSecurityDialog(designTimeContext);
        }
      }


      securityDialog.setJdbcPasswordField(dialogModel.getJdbcPasswordField());
      securityDialog.setJdbcUserField(dialogModel.getJdbcUserField());

      if (securityDialog.performEdit())
      {
        dialogModel.setJdbcUserField(securityDialog.getJdbcUserField());
        dialogModel.setJdbcPasswordField(securityDialog.getJdbcPasswordField());
      }
    }
  }

  private JdbcSecurityDialog securityDialog;

  public JdbcConnectionPanel(final DataSourceDialogModel aDialogModel, final DesignTimeContext designTimeContext)
  {
    super(aDialogModel, designTimeContext);
    initPanel();
  }

  protected Action createEditSecurityAction()
  {
    return new EditSecurityAction();
  }

  public JdbcConnectionDefinition createConnectionDefinition(final ConnectionProvider currentJNDISource)
  {
    final DataSourceDialogModel dialogModel = getDialogModel();
    if (currentJNDISource instanceof DriverConnectionProvider)
    {
      final DriverConnectionProvider dcp = (DriverConnectionProvider) currentJNDISource;
      final ListModel model = dialogModel.getConnections();
      for (int i = 0; i < model.getSize(); i++)
      {
        final JdbcConnectionDefinition definition = (JdbcConnectionDefinition) model.getElementAt(i);
        if (definition instanceof DriverConnectionDefinition == false)
        {
          continue;
        }
        final DriverConnectionDefinition dcd = (DriverConnectionDefinition) definition;
        if (ObjectUtilities.equal(dcd.getDriverClass(), dcp.getDriver()) &&
            ObjectUtilities.equal(dcd.getUsername(), dcp.getProperty("user")) &&
            ObjectUtilities.equal(dcd.getPassword(), dcp.getProperty("password")) &&
            ObjectUtilities.equal(dcd.getConnectionString(), dcp.getUrl()) &&
            ObjectUtilities.equal(dcd.getName(), dcp.getProperty("::pentaho-reporting::name")))
        {
          return definition;
        }
      }

      String customName = dcp.getProperty("::pentaho-reporting::name");
      if (customName == null)
      {
        customName = getBundleSupport().getString("JdbcConnectionPanel.CustomConnection");
      }

      final String[] strings = dcp.getPropertyNames();
      final Properties p = new Properties();
      for (int i = 0; i < strings.length; i++)
      {
        final String string = strings[i];
        p.put(string, dcp.getProperty(string));
      }

      return new DriverConnectionDefinition
          (customName, dcp.getDriver(), dcp.getUrl(),
              dcp.getProperty("user"), dcp.getProperty("password"),
              dcp.getProperty("::pentaho-reporting::hostname"),
              dcp.getProperty("::pentaho-reporting::database-name"),
              dcp.getProperty("::pentaho-reporting::database-type"),
              dcp.getProperty("::pentaho-reporting::port"),
              p);
    }
    else if (currentJNDISource instanceof JndiConnectionProvider)
    {
      final JndiConnectionProvider jcp = (JndiConnectionProvider) currentJNDISource;
      final ListModel model = dialogModel.getConnections();
      for (int i = 0; i < model.getSize(); i++)
      {
        final JdbcConnectionDefinition definition = (JdbcConnectionDefinition) model.getElementAt(i);
        if (definition instanceof JndiConnectionDefinition == false)
        {
          continue;
        }
        final JndiConnectionDefinition dcd = (JndiConnectionDefinition) definition;

        if (ObjectUtilities.equal(dcd.getJndiName(), jcp.getConnectionPath()))
        {
          return dcd;
        }
      }
      return new JndiConnectionDefinition(getBundleSupport().getString("JdbcConnectionPanel.CustomConnection"),
          jcp.getConnectionPath(), null, jcp.getUsername(), jcp.getPassword());
    }
    return null;
  }

}
