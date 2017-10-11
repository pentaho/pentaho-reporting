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

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SimpleSQLReportDataFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.ui.datasources.jdbc.JdbcDataSourceModule;
import org.pentaho.reporting.ui.datasources.jdbc.connection.DriverConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JndiConnectionDefinition;

public class SimpleJdbcDataSourceDialog extends CommonDialog
{

  private class ConfirmValidateHandler implements PropertyChangeListener
  {
    private ConfirmValidateHandler()
    {
    }

    public void propertyChange(final PropertyChangeEvent evt)
    {
      validateInputs(false);
    }
  }

  protected static final Log log = LogFactory.getLog(JdbcDataSourceDialog.class);

  private SimpleDataSourceDialogModel dialogModel;
  private ResourceBundleSupport bundleSupport;
  private DesignTimeContext designTimeContext;
  private JdbcConnectionPanel connectionComponent;

  public SimpleJdbcDataSourceDialog(final DesignTimeContext designTimeContext)
  {
    this.designTimeContext = designTimeContext;
    initDialog();
  }

  public SimpleJdbcDataSourceDialog(final DesignTimeContext designTimeContext, final JDialog parent)
  {
    super(parent);
    this.designTimeContext = designTimeContext;
    initDialog();
  }

  public SimpleJdbcDataSourceDialog(final DesignTimeContext designTimeContext, final JFrame parent)
  {
    super(parent);
    this.designTimeContext = designTimeContext;
    initDialog();
  }

  public DesignTimeContext getDesignTimeContext()
  {
    return designTimeContext;
  }

  /**
   * Displays the dialog and returns the newly created JNDIDataSetReportElement
   *
   * @param dataFactory the datafactory to be configured or null to create a new one
   * @return the a clone of the configured datafactory or null on cancel.
   */
  public SimpleSQLReportDataFactory performConfiguration(final SimpleSQLReportDataFactory dataFactory)
  {
    dialogModel.clear();

    // Load the data from the current report element
    if (dataFactory != null)
    {
      dialogModel.setJdbcPasswordField(dataFactory.getPasswordField());
      dialogModel.setJdbcUserField(dataFactory.getUserField());

      final ConnectionProvider currentJNDISource = dataFactory.getConnectionProvider();
      final JdbcConnectionDefinition definition = connectionComponent.createConnectionDefinition(currentJNDISource);
      dialogModel.addConnection(definition);
      dialogModel.getConnections().setSelectedItem(definition);
    }

    // Enable the dialog
    if (performEdit() == false)
    {
      return null;
    }

    final JdbcConnectionDefinition connectionDefinition =
        (JdbcConnectionDefinition) dialogModel.getConnections().getSelectedItem();

    if (connectionDefinition == null)
    {
      return null;
    }

    final SimpleSQLReportDataFactory newDataFactory;

    if (connectionDefinition instanceof JndiConnectionDefinition)
    {
      final JndiConnectionDefinition jcd = (JndiConnectionDefinition) connectionDefinition;
      final JndiConnectionProvider provider = new JndiConnectionProvider();
      provider.setConnectionPath(jcd.getJndiName());
      provider.setUsername(jcd.getUsername());
      provider.setPassword(jcd.getPassword());
      newDataFactory = new SimpleSQLReportDataFactory(provider);
    }
    else if (connectionDefinition instanceof DriverConnectionDefinition)
    {
      final DriverConnectionDefinition dcd = (DriverConnectionDefinition) connectionDefinition;
      final DriverConnectionProvider provider = new DriverConnectionProvider();
      provider.setDriver(dcd.getDriverClass());
      provider.setUrl(dcd.getConnectionString());

      final Properties properties = dcd.getProperties();
      final Enumeration keys = properties.keys();
      while (keys.hasMoreElements())
      {
        final String key = (String) keys.nextElement();
        provider.setProperty(key, properties.getProperty(key));
      }

      newDataFactory = new SimpleSQLReportDataFactory(provider);
    }
    else
    {
      return null;
    }

    newDataFactory.setPasswordField(dialogModel.getJdbcPasswordField());
    newDataFactory.setUserField(dialogModel.getJdbcUserField());
    return newDataFactory;
  }

  /**
   * Creates the panel which holds the main content of the dialog
   */
  private void initDialog()
  {
    dialogModel = new SimpleDataSourceDialogModel();
    dialogModel.addPropertyChangeListener(new ConfirmValidateHandler());

    bundleSupport = new ResourceBundleSupport(Locale.getDefault(), JdbcDataSourceModule.MESSAGES,
        ObjectUtilities.getClassLoader(JdbcDataSourceModule.class));
    connectionComponent = new JdbcConnectionPanel(dialogModel, designTimeContext);

    setTitle(bundleSupport.getString("JdbcDataSourceDialog.Title"));
    setModal(true);


    // Add some padding around the final containers
    // Return the center panel
    super.init();
  }

  protected String getDialogId()
  {
    return "JdbcDataSourceEditor.Simple";
  }

  protected Component createContentPane()
  {

    final JLabel descriptionLabel = new JLabel(bundleSupport.getString("SimpleJdbcDataSourceDialog.DescriptionLabel"));

    final JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.add(BorderLayout.CENTER, descriptionLabel);
    headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    // Create the connection panel

    // Create the content panel
    final JPanel contentPanel = new JPanel(new BorderLayout());
    // add padding between the containers
    contentPanel.add(BorderLayout.CENTER, connectionComponent);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    return contentPanel;
  }

  protected SimpleDataSourceDialogModel getDialogModel()
  {
    return dialogModel;
  }

  protected ResourceBundleSupport getBundleSupport()
  {
    return bundleSupport;
  }
}
