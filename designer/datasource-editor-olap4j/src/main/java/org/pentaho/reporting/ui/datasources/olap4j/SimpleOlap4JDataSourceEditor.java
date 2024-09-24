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

package org.pentaho.reporting.ui.datasources.olap4j;


import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinitionManager;
import org.pentaho.reporting.ui.datasources.jdbc.ui.SimpleDataSourceDialogModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Michael D'Amour
 */
public abstract class SimpleOlap4JDataSourceEditor extends CommonDialog {
  private class ConfirmEnabledHandler implements PropertyChangeListener {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ConfirmEnabledHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      final SimpleDataSourceDialogModel dialogModel = getDialogModel();
      getConfirmAction().setEnabled( dialogModel.isConnectionSelected() );
    }
  }

  private boolean confirmed;
  private SimpleDataSourceDialogModel dialogModel;
  private OlapConnectionPanel connectionComponent;

  public SimpleOlap4JDataSourceEditor( final DesignTimeContext context ) {
    init( context );
  }

  public SimpleOlap4JDataSourceEditor( final DesignTimeContext context, final Dialog owner ) {
    super( owner );
    init( context );
  }

  public SimpleOlap4JDataSourceEditor( final DesignTimeContext context, final Frame owner ) {
    super( owner );
    init( context );
  }

  protected void init( final DesignTimeContext designTimeContext ) {
    setModal( true );

    dialogModel = new SimpleDataSourceDialogModel
      ( new JdbcConnectionDefinitionManager( "org/pentaho/reporting/ui/datasources/olap4j/Settings" ) );

    connectionComponent = new OlapConnectionPanel( dialogModel, designTimeContext );
    connectionComponent.setBorder( BorderFactory.createEmptyBorder( 0, 8, 0, 8 ) );

    // Create the button panel
    final ConfirmEnabledHandler confirmAction = new ConfirmEnabledHandler();
    dialogModel.addPropertyChangeListener( confirmAction );


    // Return the center panel
    super.init();
  }

  protected Component createContentPane() {
    // Create the content panel
    final JPanel contentPanel = new JPanel( new BorderLayout() );
    contentPanel.add( BorderLayout.CENTER, connectionComponent );
    contentPanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) );
    return contentPanel;
  }

  public DataFactory performConfiguration( final AbstractMDXDataFactory dataFactory ) {
    // Reset the ok / cancel flag
    dialogModel.clear();
    connectionComponent.setRoleField( null );
    confirmed = false;

    // Initialize the internal storage

    // Load the current configuration
    if ( dataFactory != null ) {

      final OlapConnectionProvider currentJNDISource = dataFactory.getConnectionProvider();
      final JdbcConnectionDefinition definition = getConnectionPanel().createConnectionDefinition( currentJNDISource );
      getDialogModel().addConnection( definition );
      getDialogModel().getConnections().setSelectedItem( definition );

      getDialogModel().setJdbcUserField( dataFactory.getJdbcUserField() );
      getDialogModel().setJdbcPasswordField( dataFactory.getJdbcPasswordField() );
      connectionComponent.setRoleField( dataFactory.getRoleField() );
    }

    // Enable the dialog
    if ( !performEdit() ) {
      return null;
    }

    final AbstractMDXDataFactory factory = createDataFactory();
    if ( factory == null ) {
      return null;
    }
    factory.setJdbcUserField( getDialogModel().getJdbcUserField() );
    factory.setJdbcPasswordField( getDialogModel().getJdbcPasswordField() );
    factory.setRoleField( connectionComponent.getRoleField() );
    return factory;
  }

  protected abstract AbstractMDXDataFactory createDataFactory();

  protected SimpleDataSourceDialogModel getDialogModel() {
    return dialogModel;
  }

  protected OlapConnectionPanel getConnectionPanel() {
    return connectionComponent;
  }

  public boolean isConfirmed() {
    return confirmed;
  }
}
