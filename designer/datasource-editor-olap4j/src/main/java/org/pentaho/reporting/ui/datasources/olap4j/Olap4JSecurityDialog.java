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


package org.pentaho.reporting.ui.datasources.olap4j;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

import javax.swing.*;
import java.awt.*;

public class Olap4JSecurityDialog extends CommonDialog {
  private JComboBox roleFieldBox;
  private JComboBox jdbcUserFieldBox;
  private JComboBox jdbcPasswordFieldBox;

  public Olap4JSecurityDialog( final DesignTimeContext context )
    throws HeadlessException {
    init( context );
  }

  public Olap4JSecurityDialog( final Frame owner, final DesignTimeContext context )
    throws HeadlessException {
    super( owner );
    init( context );
  }

  public Olap4JSecurityDialog( final Dialog owner, final DesignTimeContext context )
    throws HeadlessException {
    super( owner );
    init( context );
  }

  protected void init( final DesignTimeContext context ) {
    setTitle( Messages.getString( "Olap4JSecurityDialog.Title" ) );

    final String[] reportFields = context.getDataSchemaModel().getColumnNames();
    jdbcPasswordFieldBox = new JComboBox( reportFields );
    jdbcPasswordFieldBox.setEditable( true );

    jdbcUserFieldBox = new JComboBox( reportFields );
    jdbcUserFieldBox.setEditable( true );

    roleFieldBox = new JComboBox( reportFields );
    roleFieldBox.setEditable( true );

    super.init();
  }

  protected String getDialogId() {
    return "Olap4JDataSourceEditor.Security";
  }

  protected Component createContentPane() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    contentPane.add( new JLabel( Messages.getString( "Olap4JSecurityDialog.Role.FieldValue" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    contentPane.add( roleFieldBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    contentPane.add( new JLabel( Messages.getString( "Olap4JSecurityDialog.JDBCUser.FieldValue" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    contentPane.add( jdbcUserFieldBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    contentPane.add( new JLabel( Messages.getString( "Olap4JSecurityDialog.JDBCPassword.FieldValue" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    contentPane.add( jdbcPasswordFieldBox, gbc );
    return contentPane;
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

  public void setRoleField( final String jdbcPasswordField ) {
    roleFieldBox.setSelectedItem( jdbcPasswordField );
  }

  public boolean performEdit() {
    return super.performEdit();
  }
}

