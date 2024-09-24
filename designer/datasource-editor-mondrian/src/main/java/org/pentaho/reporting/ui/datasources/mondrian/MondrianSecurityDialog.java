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

package org.pentaho.reporting.ui.datasources.mondrian;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.GenericCellEditor;
import org.pentaho.reporting.libraries.designtime.swing.GenericCellRenderer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MondrianSecurityDialog extends CommonDialog {
  private static class RemoveParameterAction extends AbstractAction implements ListSelectionListener {
    private JTable propertiesTable;

    private RemoveParameterAction( final JTable propertiesTable ) {
      this.propertiesTable = propertiesTable;

      final URL resource = MondrianSecurityDialog.class
        .getResource( "/org/pentaho/reporting/ui/datasources/mondrian/resources/Remove.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "MondrianSecurityDialog.RemoveParameter.Name" ) );
      }

      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "MondrianSecurityDialog.RemoveAction.Description" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final int i = propertiesTable.getSelectedRow();
      if ( i == -1 ) {
        return;
      }

      final MondrianPropertiesTableModel tableModel = (MondrianPropertiesTableModel) propertiesTable.getModel();
      tableModel.removeRow( i );
    }

    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( propertiesTable.getSelectedRow() != -1 );
    }
  }


  private static class AddParameterAction extends AbstractAction {
    private JTable propertiesTable;

    private AddParameterAction( final JTable propertiesTable ) {
      this.propertiesTable = propertiesTable;
      final URL resource =
        MondrianSecurityDialog.class.getResource( "/org/pentaho/reporting/ui/datasources/mondrian/resources/Add.png" );
      if ( resource != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( resource ) );
      } else {
        putValue( Action.NAME, Messages.getString( "MondrianSecurityDialog.AddParameter.Name" ) );
      }

      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "MondrianSecurityDialog.AddAction.Description" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final MondrianPropertiesTableModel tableModel = (MondrianPropertiesTableModel) propertiesTable.getModel();
      tableModel.addRow();
    }
  }

  private static class MondrianPropertiesTableModel extends AbstractTableModel {
    private Properties connectionProperties;
    private ArrayList<Map.Entry<String, String>> data;

    private MondrianPropertiesTableModel() {
      connectionProperties = new Properties();
      data = new ArrayList<Map.Entry<String, String>>();
    }

    public Properties getProperties() {
      Properties mondrianProperties = new Properties();

      for ( final Map.Entry<String, String> entry : data ) {
        final String key = entry.getKey();
        final String value = entry.getValue();

        if ( ( !key.isEmpty() ) && ( !value.isEmpty() ) ) {
          mondrianProperties.put( key, value );
        }
      }

      return mondrianProperties;
    }


    public void setMondrianProperties( final Properties properties ) {
      connectionProperties = properties;

      if ( properties == null ) {
        return;
      }

      // Update table with new data
      final Enumeration<Object> e = properties.keys();
      while ( e.hasMoreElements() ) {
        final String key = (String) e.nextElement();
        final String value = (String) properties.get( key );

        final Map.Entry<String, String> rowData = new HashMap.SimpleEntry<String, String>( key, value );
        if ( data.contains( rowData ) == false ) {
          data.add( rowData );
        }
      }

      fireTableDataChanged();
    }

    public void addRow() {
      final Map.Entry<String, String> rowData = new HashMap.SimpleEntry<String, String>( "", "" );
      data.add( rowData );

      fireTableDataChanged();
    }

    public void removeRow( final int rowIndex ) {
      final Map.Entry<String, String> rowData = data.get( rowIndex );
      if ( rowData != null ) {
        data.remove( rowIndex );
      }

      fireTableDataChanged();
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    public Class getColumnClass( final int columnIndex ) {
      return String.class;
    }

    public String getColumnName( final int columnIndex ) {
      switch( columnIndex ) {
        case 0:
          return Messages.getString( "MondrianSecurityDialog.TableColumn.Key" );
        case 1:
          return Messages.getString( "MondrianSecurityDialog.TableColumn.Value" );
        default:
          throw new IndexOutOfBoundsException();
      }
    }

    public Object getValueAt( final int rowIndex, final int columnIndex ) {
      final Map.Entry<String, String> rowData = data.get( rowIndex );
      if ( rowData != null ) {
        return ( columnIndex == 0 ) ? rowData.getKey() : rowData.getValue();
      }

      return null;
    }

    /**
     * Set value at row & column
     *
     * @param aValue      value to assign to cell
     * @param rowIndex    row of cell
     * @param columnIndex column of cell
     */
    public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
      if ( aValue == null ) {
        return;
      }

      Map.Entry<String, String> rowData = data.get( rowIndex );
      if ( rowData != null ) {
        if ( columnIndex == 0 ) {
          final String colData = (String) getValueAt( rowIndex, 1 );
          final String paramValue = colData != null ? colData : "";
          if ( rowIndex < data.size() ) {
            data.remove( rowIndex );
          }

          data.add( new HashMap.SimpleEntry<String, String>( (String) aValue, paramValue ) );
        } else {
          rowData.setValue( (String) aValue );
        }
      }

      fireTableCellUpdated( rowIndex, columnIndex );
    }


    public int getRowCount() {
      return data.size();
    }

    public int getColumnCount() {
      return 2;
    }

    /**
     * Returns true.  This is the default implementation for all cells.
     *
     * @param rowIndex    the row being queried
     * @param columnIndex the column being queried
     * @return false
     */
    public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
      return true;
    }
  }

  private DesignTimeContext context;
  private JTextField roleTextField;
  private JTextField jdbcUserTextField;
  private JTextField jdbcPasswordTextField;
  private JComboBox roleFieldBox;
  private JComboBox jdbcUserFieldBox;
  private JComboBox jdbcPasswordFieldBox;

  private JTable propertiesTable;

  public MondrianSecurityDialog( final DesignTimeContext context )
    throws HeadlessException {
    init( context );
  }

  public MondrianSecurityDialog( final Frame owner, final DesignTimeContext context )
    throws HeadlessException {
    super( owner );
    init( context );
  }

  public MondrianSecurityDialog( final Dialog owner, final DesignTimeContext context )
    throws HeadlessException {
    super( owner );
    init( context );
  }

  protected void init( final DesignTimeContext context ) {
    this.context = context;

    setTitle( Messages.getString( "MondrianSecurityDialog.Title" ) );

    roleTextField = new JTextField();
    roleTextField.setColumns( 35 );

    jdbcUserTextField = new JTextField();
    jdbcUserTextField.setColumns( 35 );

    jdbcPasswordTextField = new JTextField();
    jdbcPasswordTextField.setColumns( 35 );

    final String[] reportFields = context.getDataSchemaModel().getColumnNames();
    jdbcPasswordFieldBox = new JComboBox( reportFields );
    jdbcPasswordFieldBox.setEditable( true );

    jdbcUserFieldBox = new JComboBox( reportFields );
    jdbcUserFieldBox.setEditable( true );

    roleFieldBox = new JComboBox( reportFields );
    roleFieldBox.setEditable( true );

    GenericCellEditor cellEditor = new GenericCellEditor( String.class, false );

    propertiesTable = new JTable( new MondrianPropertiesTableModel() );
    propertiesTable.setDefaultEditor( String.class, cellEditor );
    propertiesTable.setDefaultRenderer( String.class, new GenericCellRenderer() );
    propertiesTable.setShowHorizontalLines( true );
    propertiesTable.setShowVerticalLines( true );
    propertiesTable.setGridColor( SystemColor.controlShadow );
    propertiesTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    propertiesTable.setPreferredScrollableViewportSize( new Dimension( 200, 100 ) );
    propertiesTable.setFillsViewportHeight( false );
    super.init();
  }

  protected String getDialogId() {
    return "MondrianDataSourceEditor.Security";
  }

  protected Component createContentPane() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );

    final JPanel securityPanel = new JPanel();
    securityPanel.setLayout( new GridBagLayout() );
    securityPanel.setBorder( new TitledBorder( Messages.getString( "MondrianSecurityDialog.SecurityProperties" ) ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.EAST;
    securityPanel.add( createRolePanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.EAST;
    securityPanel.add( createJdbcUserPanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.EAST;
    securityPanel.add( createJdbcPasswordPanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.NORTH;
    contentPane.add( securityPanel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.EAST;
    contentPane.add( createMondrianPropertiesPanel(), gbc );

    return contentPane;
  }

  private JPanel createRolePanel() {
    final JPanel rolePanel = new JPanel();
    rolePanel.setBorder( new TitledBorder( Messages.getString( "MondrianSecurityDialog.Role" ) ) );
    rolePanel.setLayout( new GridBagLayout() );
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    rolePanel.add( new JLabel( Messages.getString( "MondrianSecurityDialog.Role.StaticValue" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    rolePanel.add( new JLabel( Messages.getString( "MondrianSecurityDialog.Role.FieldValue" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.EAST;
    rolePanel.add( roleTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    rolePanel.add( roleFieldBox, gbc );
    return rolePanel;
  }

  private JPanel createJdbcPasswordPanel() {
    final JPanel passwordPanel = new JPanel();
    passwordPanel.setBorder( new TitledBorder( Messages.getString( "MondrianSecurityDialog.JDBCPassword" ) ) );
    passwordPanel.setLayout( new GridBagLayout() );
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    passwordPanel.add( new JLabel( Messages.getString( "MondrianSecurityDialog.JDBCPassword.StaticValue" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    passwordPanel.add( new JLabel( Messages.getString( "MondrianSecurityDialog.JDBCPassword.FieldValue" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    passwordPanel.add( jdbcPasswordTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    passwordPanel.add( jdbcPasswordFieldBox, gbc );
    return passwordPanel;
  }

  private JPanel createJdbcUserPanel() {
    final JPanel userPanel = new JPanel();
    userPanel.setBorder( new TitledBorder( Messages.getString( "MondrianSecurityDialog.JDBCUser" ) ) );
    userPanel.setLayout( new GridBagLayout() );
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    userPanel.add( new JLabel( Messages.getString( "MondrianSecurityDialog.JDBCUser.StaticValue" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    userPanel.add( new JLabel( Messages.getString( "MondrianSecurityDialog.JDBCUser.FieldValue" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    userPanel.add( jdbcUserTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    userPanel.add( jdbcUserFieldBox, gbc );
    return userPanel;
  }

  public String getRole() {
    return roleTextField.getText();
  }

  public void setRole( final String role ) {
    roleTextField.setText( role );
  }

  private JPanel createMondrianPropertiesPanel() {
    final JPanel mondrianPropertiesPanel = new JPanel();
    mondrianPropertiesPanel
      .setBorder( new TitledBorder( Messages.getString( "MondrianSecurityDialog.MondrianProperties" ) ) );
    mondrianPropertiesPanel.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    final JPanel propertiesButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 5 ) );
    propertiesButtonPanel.add( new BorderlessButton( new AddParameterAction( propertiesTable ) ) );
    propertiesButtonPanel.add( new BorderlessButton( new RemoveParameterAction( propertiesTable ) ) );
    mondrianPropertiesPanel.add( propertiesButtonPanel, gbc );


    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    mondrianPropertiesPanel.add( new JScrollPane( propertiesTable ), gbc );

    return mondrianPropertiesPanel;
  }

  public String getRoleField() {
    final Object o = roleFieldBox.getSelectedItem();
    if ( o instanceof String == false ) {
      return null;
    }
    final String field = (String) o;
    if ( StringUtils.isEmpty( field ) ) {
      return null;
    }
    return field;
  }

  public void setRoleField( final String roleField ) {
    roleFieldBox.setSelectedItem( roleField );
  }

  public String getJdbcUser() {
    return jdbcUserTextField.getText();
  }

  public void setJdbcUser( final String jdbcUser ) {
    jdbcUserTextField.setText( jdbcUser );
  }

  public String getJdbcUserField() {
    final Object o = jdbcUserFieldBox.getSelectedItem();
    if ( o instanceof String == false ) {
      return null;
    }
    final String field = (String) o;
    if ( StringUtils.isEmpty( field ) ) {
      return null;
    }
    return field;
  }

  public void setJdbcUserField( final String jdbcUserField ) {
    jdbcUserFieldBox.setSelectedItem( jdbcUserField );
  }

  public String getJdbcPassword() {
    return jdbcPasswordTextField.getText();
  }

  public void setJdbcPassword( final String jdbcPassword ) {
    jdbcPasswordTextField.setText( jdbcPassword );
  }

  public String getJdbcPasswordField() {
    final Object o = jdbcPasswordFieldBox.getSelectedItem();
    if ( o instanceof String == false ) {
      return null;
    }
    final String field = (String) o;
    if ( StringUtils.isEmpty( field ) ) {
      return null;
    }
    return field;
  }

  public void setJdbcPasswordField( final String jdbcPasswordField ) {
    jdbcPasswordFieldBox.setSelectedItem( jdbcPasswordField );
  }

  public Properties getMondrianProperties() {
    return ( (MondrianPropertiesTableModel) propertiesTable.getModel() ).getProperties();
  }

  public void setMondrianProperties( final Properties properties ) {
    ( (MondrianPropertiesTableModel) propertiesTable.getModel() ).setMondrianProperties( properties );
  }

  public boolean performEdit() {
    return super.performEdit();
  }
}
