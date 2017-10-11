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
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.ui.datasources.jdbc.JdbcDataSourceModule;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.ui.xul.XulException;

public abstract class ConnectionPanel extends JPanel
{
  private class DataSourceDefinitionListSelectionListener implements ListSelectionListener
  {
    private JList dataSourceList;

    private DataSourceDefinitionListSelectionListener(final JList dataSourceList)
    {
      this.dataSourceList = dataSourceList;
    }

    public void valueChanged(final ListSelectionEvent e)
    {
      getDialogModel().getConnections().setSelectedItem(dataSourceList.getSelectedValue());
    }
  }

  private static class DataSourceDefinitionListCellRenderer extends DefaultListCellRenderer
  {
    public Component getListCellRendererComponent(final JList list,
                                                  final Object value,
                                                  final int index,
                                                  final boolean isSelected,
                                                  final boolean cellHasFocus)
    {
      final JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index,
          isSelected,
          cellHasFocus);
      if (value != null)
      {
        final String jndiName = ((JdbcConnectionDefinition) value).getName();
        if (!"".equals(jndiName))
        {
          listCellRendererComponent.setText(jndiName);
        }
        else
        {
          listCellRendererComponent.setText(" ");
        }
      }
      return listCellRendererComponent;
    }
  }

  private static class SelectionConnectionUpdateHandler implements PropertyChangeListener
  {
    private JList dataSourceList;

    private SelectionConnectionUpdateHandler(final JList dataSourceList)
    {
      this.dataSourceList = dataSourceList;
    }

    public void propertyChange(final PropertyChangeEvent aEvent)
    {
      final DataSourceDialogModel theDialogModel = (DataSourceDialogModel) aEvent.getSource();
      final DefaultComboBoxModel theConnections = theDialogModel.getConnections();
      final Object theConnection = theConnections.getSelectedItem();
      if (theConnection != null)
      {
        dataSourceList.setSelectedValue(theConnection, true);
      }
      else
      {
        dataSourceList.clearSelection();
      }
    }
  }

  private class EditDataSourceAction extends AbstractAction implements PropertyChangeListener
  {
    private JList dataSourceList;

    private EditDataSourceAction(final JList dataSourceList)
    {
      this.dataSourceList = dataSourceList;
      final URL location =
          ConnectionPanel.class.getResource("/org/pentaho/reporting/ui/datasources/jdbc/resources/Edit.png");
      if (location != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(location));
      }
      else
      {
        putValue(Action.NAME, bundleSupport.getString("ConnectionPanel.Edit.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, bundleSupport.getString("ConnectionPanel.Edit.Description"));
      setEnabled(getDialogModel().isConnectionSelected());
    }

    public void propertyChange(final PropertyChangeEvent evt)
    {
      setEnabled(getDialogModel().isConnectionSelected());
    }

    public void actionPerformed(final ActionEvent e)
    {
      final JdbcConnectionDefinition existingConnection =
          (JdbcConnectionDefinition) dataSourceList.getSelectedValue();

      final DesignTimeContext designTimeContext = getDesignTimeContext();
      try
      {
        final Window parentWindow = LibSwingUtil.getWindowAncestor(ConnectionPanel.this);
        final XulDatabaseDialog connectionDialog = new XulDatabaseDialog(parentWindow, designTimeContext);
        final JdbcConnectionDefinition connectionDefinition = connectionDialog.open(existingConnection);

        // See if the edit completed...
        if (connectionDefinition != null)
        {
          // If the name changed, delete it before the update is performed
          if (existingConnection.getName().equals(connectionDefinition.getName()) == false)
          {
            getDialogModel().getConnectionDefinitionManager().removeSource(existingConnection.getName());
          }
          final DataSourceDialogModel dialogModel = getDialogModel();
          // Add / update the JNDI source
          getDialogModel().getConnectionDefinitionManager().updateSourceList(connectionDefinition);

          dialogModel.editConnection(existingConnection, connectionDefinition);
          dataSourceList.setSelectedValue(connectionDefinition, true);
        }
      }
      catch (XulException e1)
      {
        designTimeContext.error(e1);
      }
    }
  }

  private class RemoveDataSourceAction extends AbstractAction implements PropertyChangeListener
  {
    private JList dataSourceList;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     *
     * @param dataSourceList the list containing the datasources
     */
    private RemoveDataSourceAction(final JList dataSourceList)
    {
      this.dataSourceList = dataSourceList;
      setEnabled(getDialogModel().isConnectionSelected());
      final URL resource = ConnectionPanel.class.getResource("/org/pentaho/reporting/ui/datasources/jdbc/resources/Remove.png");
      if (resource != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(resource));
      }
      else
      {
        putValue(Action.NAME, bundleSupport.getString("ConnectionPanel.Remove.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, bundleSupport.getString("ConnectionPanel.Remove.Description"));
    }

    public void propertyChange(final PropertyChangeEvent evt)
    {
      setEnabled(getDialogModel().isConnectionSelected());
    }

    public void actionPerformed(final ActionEvent e)
    {
      final JdbcConnectionDefinition source = (JdbcConnectionDefinition) dataSourceList.getSelectedValue();
      if (source != null)
      {
        getDialogModel().getConnectionDefinitionManager().removeSource(source.getName());
        getDialogModel().removeConnection(source);
      }
    }
  }

  private class AddDataSourceAction extends AbstractAction
  {
    private JList dataSourceList;

    private AddDataSourceAction(final JList dataSourceList)
    {
      this.dataSourceList = dataSourceList;
      final URL location = ConnectionPanel.class.getResource(
          "/org/pentaho/reporting/ui/datasources/jdbc/resources/Add.png");
      if (location != null)
      {
        putValue(Action.SMALL_ICON, new ImageIcon(location));
      }
      else
      {
        putValue(Action.NAME, bundleSupport.getString("ConnectionPanel.Add.Name"));
      }
      putValue(Action.SHORT_DESCRIPTION, bundleSupport.getString("ConnectionPanel.Add.Description"));
    }

    public void actionPerformed(final ActionEvent e)
    {
      final DesignTimeContext designTimeContext = getDesignTimeContext();
      try
      {
        final Window parentWindow = LibSwingUtil.getWindowAncestor(ConnectionPanel.this);
        final XulDatabaseDialog connectionDialog = new XulDatabaseDialog(parentWindow, designTimeContext);
        final JdbcConnectionDefinition connectionDefinition = connectionDialog.open(null);

        if (connectionDefinition != null &&
            !StringUtils.isEmpty(connectionDefinition.getName()))
        {
          // A new JNDI source was created
          if (getDialogModel().getConnectionDefinitionManager().updateSourceList(connectionDefinition) == false)
          {
            getDialogModel().addConnection(connectionDefinition);
            dataSourceList.setSelectedValue(connectionDefinition, true);
          }
        }
      }
      catch (XulException e1)
      {
        designTimeContext.error(e1);
      }
    }
  }

  private DataSourceDialogModel dialogModel;
  private DesignTimeContext designTimeContext;
  private ResourceBundleSupport bundleSupport;
  private boolean securityConfigurationAvailable;

  public ConnectionPanel(final DataSourceDialogModel aDialogModel,
                         final DesignTimeContext designTimeContext)
  {
    this.securityConfigurationAvailable = true;
    this.dialogModel = aDialogModel;
    this.designTimeContext = designTimeContext;
    this.bundleSupport = new ResourceBundleSupport(Locale.getDefault(), JdbcDataSourceModule.MESSAGES,
        ObjectUtilities.getClassLoader(JdbcDataSourceModule.class));
  }

  protected void initPanel()
  {
    setLayout(new BorderLayout());

    final JList dataSourceList = new JList(dialogModel.getConnections());
    dataSourceList.setCellRenderer(new DataSourceDefinitionListCellRenderer());
    dataSourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    dataSourceList.addListSelectionListener(new DataSourceDefinitionListSelectionListener(dataSourceList));
    dataSourceList.setVisibleRowCount(10);

    final SelectionConnectionUpdateHandler theSelectedConnectionAction = new SelectionConnectionUpdateHandler(
        dataSourceList);
    dialogModel.addPropertyChangeListener(theSelectedConnectionAction);

    final EditDataSourceAction editDataSourceAction = new EditDataSourceAction(dataSourceList);
    dialogModel.addPropertyChangeListener(editDataSourceAction);

    final RemoveDataSourceAction removeDataSourceAction = new RemoveDataSourceAction(dataSourceList);
    dialogModel.addPropertyChangeListener(removeDataSourceAction);

    final JPanel connectionButtonPanel = new JPanel();
    connectionButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    if (isSecurityConfigurationAvailable())
    {
      connectionButtonPanel.add(new JButton(createEditSecurityAction()));
      connectionButtonPanel.add(Box.createHorizontalStrut(40));
    }
    connectionButtonPanel.add(new BorderlessButton(editDataSourceAction));
    connectionButtonPanel.add(new BorderlessButton(new AddDataSourceAction(dataSourceList)));
    connectionButtonPanel.add(new BorderlessButton(removeDataSourceAction));

    final JPanel connectionButtonPanelWrapper = new JPanel(new BorderLayout());
    connectionButtonPanelWrapper.add(new JLabel(bundleSupport.getString("ConnectionPanel.Connections")), BorderLayout.CENTER);
    connectionButtonPanelWrapper.add(connectionButtonPanel, BorderLayout.EAST);

    add(BorderLayout.NORTH, connectionButtonPanelWrapper);
    add(BorderLayout.CENTER, new JScrollPane(dataSourceList));
  }

  protected abstract Action createEditSecurityAction();

  public boolean isSecurityConfigurationAvailable()
  {
    return securityConfigurationAvailable;
  }

  public void setSecurityConfigurationAvailable(final boolean securityConfigurationAvailable)
  {
    this.securityConfigurationAvailable = securityConfigurationAvailable;
  }

  public DataSourceDialogModel getDialogModel()
  {
    return dialogModel;
  }

  public DesignTimeContext getDesignTimeContext()
  {
    return designTimeContext;
  }

  protected ResourceBundleSupport getBundleSupport()
  {
    return bundleSupport;
  }
}
