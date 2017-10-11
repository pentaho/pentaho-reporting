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

package org.pentaho.reporting.designer.core.settings.ui;

import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.settings.SettingsMessages;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ShowPasswordsDialog extends CommonDialog {
  private static class PasswordTableModel extends AbstractTableModel {
    private GlobalAuthenticationStore globalAuthenticationStore;
    private boolean showPasswords;

    private PasswordTableModel() {
    }

    public GlobalAuthenticationStore getGlobalAuthenticationStore() {
      return globalAuthenticationStore;
    }

    public void setGlobalAuthenticationStore( final GlobalAuthenticationStore globalAuthenticationStore ) {
      this.globalAuthenticationStore = globalAuthenticationStore;
      fireTableDataChanged();
    }

    public boolean isShowPasswords() {
      return showPasswords;
    }

    public void setShowPasswords( final boolean showPasswords ) {
      this.showPasswords = showPasswords;
      fireTableStructureChanged();
    }

    /**
     * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
      if ( globalAuthenticationStore == null ) {
        return 0;
      }

      final String[] strings = globalAuthenticationStore.getKnownURLs();
      return strings.length;
    }

    /**
     * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns
     * it should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
      if ( showPasswords ) {
        return 3;
      }
      return 2;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt( final int rowIndex, final int columnIndex ) {
      if ( globalAuthenticationStore == null ) {
        return null;
      }

      final String[] strings = globalAuthenticationStore.getKnownURLs();
      if ( rowIndex >= strings.length ) {
        return null;
      }
      final String url = strings[ rowIndex ];
      if ( columnIndex == 0 ) {
        return ( url );
      } else if ( columnIndex == 1 ) {
        return globalAuthenticationStore.getUsername( url );
      } else if ( columnIndex == 2 ) {
        return globalAuthenticationStore.getPassword( url );
      }
      return null;
    }

    /**
     * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc.  If
     * <code>column</code> cannot be found, returns an empty string.
     *
     * @param columnIndex the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public String getColumnName( final int columnIndex ) {
      if ( columnIndex == 0 ) {
        return SettingsMessages.getInstance().getString( "ShowPasswordsDialog.URL" );
      } else if ( columnIndex == 1 ) {
        return SettingsMessages.getInstance().getString( "ShowPasswordsDialog.Username" );
      } else if ( columnIndex == 2 ) {
        return SettingsMessages.getInstance().getString( "ShowPasswordsDialog.Password" );
      }
      return super.getColumnName( columnIndex );
    }
  }

  private class ShowPasswordsAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ShowPasswordsAction() {
      putValue( Action.NAME, SettingsMessages.getInstance().getString( "ShowPasswordsDialog.ShowPasswords" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      tableModel.setShowPasswords( true );
    }
  }

  private class RemovePasswordsAction extends AbstractAction implements ListSelectionListener {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private RemovePasswordsAction() {
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getRemoveIcon() );
      putValue( Action.SHORT_DESCRIPTION,
        SettingsMessages.getInstance().getString( "ShowPasswordsDialog.RemovePassword" ) );
      setEnabled( false );
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      final int[] selectedRows = passwordTable.getSelectedRows();
      setEnabled( selectedRows.length > 0 );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( globalAuthenticationStore == null ) {
        return;
      }
      final int[] selectedRows = passwordTable.getSelectedRows();
      final String[] selectedURLs = new String[ selectedRows.length ];
      for ( int i = 0; i < selectedURLs.length; i++ ) {
        selectedURLs[ i ] = (String) tableModel.getValueAt( selectedRows[ i ], 0 );
      }

      for ( int i = 0; i < selectedURLs.length; i++ ) {
        final String selectedURL = selectedURLs[ i ];
        if ( selectedURL != null ) {
          globalAuthenticationStore.removeCredentials( selectedURL );
        }
      }
      tableModel.fireTableDataChanged();
    }
  }

  private PasswordTableModel tableModel;
  private JTable passwordTable;
  private GlobalAuthenticationStore globalAuthenticationStore;
  private RemovePasswordsAction removeAction;

  public ShowPasswordsDialog()
    throws HeadlessException {
    init();
  }

  public ShowPasswordsDialog( final Frame owner ) {
    super( owner );
    init();
  }


  public ShowPasswordsDialog( final Dialog owner ) {
    super( owner );
    init();
  }

  public void init() {
    setTitle( SettingsMessages.getInstance().getString( "ShowPasswordsDialog.Title" ) );

    removeAction = new RemovePasswordsAction();

    tableModel = new PasswordTableModel();
    passwordTable = new JTable( tableModel );
    passwordTable.getSelectionModel().addListSelectionListener( removeAction );

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.ShowPasswords";
  }

  protected Component createContentPane() {
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    buttonPanel.add( new BorderlessButton( removeAction ) );

    final JPanel headerPanel = new JPanel();
    headerPanel.setBorder( BorderFactory.createEmptyBorder( 0, 0, 2, 0 ) );
    headerPanel.setLayout( new BorderLayout() );
    headerPanel.add( buttonPanel, BorderLayout.EAST );

    final JPanel panel = new JPanel();
    panel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    panel.setLayout( new BorderLayout() );
    panel.add( headerPanel, BorderLayout.NORTH );
    panel.add( new JScrollPane( passwordTable ), BorderLayout.CENTER );

    return panel;
  }

  protected Action[] getExtraActions() {
    return new Action[] { new ShowPasswordsAction() };
  }

  public void showDialog( final GlobalAuthenticationStore authenticationStore ) {
    this.globalAuthenticationStore = authenticationStore;
    tableModel.setGlobalAuthenticationStore( authenticationStore );
    tableModel.setShowPasswords( false );
    performEdit();
  }

  protected boolean hasCancelButton() {
    return false;
  }
}
