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
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.ui.datasources.jdbc.connection.DriverConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JndiConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.ui.ConnectionPanel;
import org.pentaho.reporting.ui.datasources.jdbc.ui.DataSourceDialogModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Properties;

public class OlapConnectionPanel extends ConnectionPanel {
  private class EditSecurityAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private EditSecurityAction() {
      putValue( Action.NAME, Messages.getString( "ConnectionPanel.EditSecurityAction.Name" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final DesignTimeContext designTimeContext = getDesignTimeContext();
      final DataSourceDialogModel dialogModel = getDialogModel();
      if ( securityDialog == null ) {
        final Window window = LibSwingUtil.getWindowAncestor( OlapConnectionPanel.this );
        if ( window instanceof Frame ) {
          securityDialog = new Olap4JSecurityDialog( (Frame) window, designTimeContext );
        } else if ( window instanceof Dialog ) {
          securityDialog = new Olap4JSecurityDialog( (Dialog) window, designTimeContext );
        } else {
          securityDialog = new Olap4JSecurityDialog( designTimeContext );
        }
      }

      securityDialog.setRoleField( roleField );
      securityDialog.setJdbcPasswordField( dialogModel.getJdbcPasswordField() );
      securityDialog.setJdbcUserField( dialogModel.getJdbcUserField() );

      if ( securityDialog.performEdit() ) {
        roleField = securityDialog.getRoleField();
        dialogModel.setJdbcUserField( securityDialog.getJdbcUserField() );
        dialogModel.setJdbcPasswordField( securityDialog.getJdbcPasswordField() );
      }
    }
  }

  private Olap4JSecurityDialog securityDialog;
  private String roleField;

  public OlapConnectionPanel( final DataSourceDialogModel aDialogModel, final DesignTimeContext designTimeContext ) {
    super( aDialogModel, designTimeContext );
    initPanel();
  }

  public JdbcConnectionDefinition createConnectionDefinition( final OlapConnectionProvider currentJNDISource ) {
    final DataSourceDialogModel dialogModel = getDialogModel();
    if ( currentJNDISource instanceof DriverConnectionProvider ) {
      final DriverConnectionProvider dcp = (DriverConnectionProvider) currentJNDISource;
      final ListModel model = dialogModel.getConnections();
      for ( int i = 0; i < model.getSize(); i++ ) {
        final JdbcConnectionDefinition definition = (JdbcConnectionDefinition) model.getElementAt( i );
        if ( definition instanceof DriverConnectionDefinition == false ) {
          continue;
        }
        final DriverConnectionDefinition dcd = (DriverConnectionDefinition) definition;
        if ( ObjectUtilities.equal( dcd.getUsername(), dcp.getProperty( "JdbcUser" ) ) &&
          ObjectUtilities.equal( dcd.getPassword(), dcp.getProperty( "JdbcPassword" ) ) &&
          ObjectUtilities.equal( dcd.getConnectionString(), dcp.getProperty( "Jdbc" ) ) &&
          ObjectUtilities.equal( dcd.getDriverClass(), dcp.getProperty( "JdbcDrivers" ) ) &&
          ObjectUtilities.equal( dcd.getName(), dcp.getProperty( "::pentaho-reporting::name" ) ) ) {
          return definition;
        }
      }

      String customName = dcp.getProperty( "::pentaho-reporting::name" );
      if ( customName == null ) {
        customName = Messages.getString( "Olap4JDataSourceEditor.CustomConnection" );
      }

      final String[] strings = dcp.getPropertyNames();
      final Properties p = new Properties();
      for ( int i = 0; i < strings.length; i++ ) {
        final String string = strings[ i ];
        p.put( string, dcp.getProperty( string ) );
      }

      return new DriverConnectionDefinition
        ( customName, dcp.getDriver(), dcp.getUrl(),
          dcp.getProperty( "user" ), dcp.getProperty( "password" ),
          dcp.getProperty( "::pentaho-reporting::hostname" ),
          dcp.getProperty( "::pentaho-reporting::database-name" ),
          dcp.getProperty( "::pentaho-reporting::database-type" ),
          dcp.getProperty( "::pentaho-reporting::port" ),
          p );
    }
    if ( currentJNDISource instanceof JndiConnectionProvider ) {
      final JndiConnectionProvider jcp = (JndiConnectionProvider) currentJNDISource;
      final ListModel model = dialogModel.getConnections();
      for ( int i = 0; i < model.getSize(); i++ ) {
        final JdbcConnectionDefinition definition = (JdbcConnectionDefinition) model.getElementAt( i );
        if ( definition instanceof JndiConnectionDefinition == false ) {
          continue;
        }
        final JndiConnectionDefinition dcd = (JndiConnectionDefinition) definition;

        if ( ObjectUtilities.equal( dcd.getJndiName(), jcp.getConnectionPath() ) ) {
          return dcd;
        }
      }
      return new JndiConnectionDefinition( Messages.getString( "Olap4JDataSourceEditor.CustomConnection" ),
        jcp.getConnectionPath(), null, jcp.getUsername(), jcp.getPassword() );
    }
    return null;
  }

  public String getRoleField() {
    return roleField;
  }

  public void setRoleField( final String roleField ) {
    this.roleField = roleField;
  }

  protected Action createEditSecurityAction() {
    return new EditSecurityAction();
  }

}
