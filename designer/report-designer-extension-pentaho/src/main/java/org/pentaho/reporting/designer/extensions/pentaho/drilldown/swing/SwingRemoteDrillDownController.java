/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.extensions.pentaho.drilldown.swing;

import org.apache.commons.lang.ObjectUtils;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterRefreshEvent;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterRefreshListener;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterTable;
import org.pentaho.reporting.designer.core.editor.drilldown.basic.DrillDownModelWrapper;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.designer.core.editor.drilldown.swing.DocumentBindingListener;
import org.pentaho.reporting.designer.extensions.pentaho.drilldown.PentahoParameterRefreshHandler;
import org.pentaho.reporting.designer.extensions.pentaho.drilldown.PentahoPathModel;
import org.pentaho.reporting.designer.extensions.pentaho.drilldown.SelectDrillTargetTask;
import org.pentaho.reporting.designer.extensions.pentaho.repository.actions.AuthenticatedServerTask;
import org.pentaho.reporting.designer.extensions.pentaho.repository.actions.LoginTask;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfileMetaData;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Optional;

/**
 * Controller of the Swing analog for sugar-xaction-drilldown.xul dialog.
 *
 * @author Aleksandr Kozlov
 */
public class SwingRemoteDrillDownController {

  /* Dialog itself. */
  private final SwingRemoteDrillDownUi drillDownUi;

  /* Model */
  private final DrillDownModelWrapper modelWrapper;

  /* Context of the report designer. */
  private final ReportDesignerContext reportDesignerContext;

  /* Xul-based class that contains login data (maybe rewrite too). --Kaa */
  private final PentahoPathModel pentahoPathWrapper;

  /* PropertyChangeListener for model. */
  private ModelWrapperUpdateHandler modelWrapperUpdateHandler;

  /* PropertyChangeListener for path wrapper. */
  private PathWrapperUpdateHandler pathWrapperUpdateHandler;

  /**
   * Create controller of the Swing analog for sugar-xaction-drilldown.xul dialog.
   * @param drillDownUi dialog itself.
   * @param reportDesignerContext context of the report designer.
   * @param modelWrapper model.
   */
  public SwingRemoteDrillDownController(
      SwingRemoteDrillDownUi drillDownUi,
      ReportDesignerContext reportDesignerContext,
      DrillDownModelWrapper modelWrapper
  ) {
    this.drillDownUi = drillDownUi;
    this.reportDesignerContext = reportDesignerContext;
    this.modelWrapper = modelWrapper;
    pentahoPathWrapper = new PentahoPathModel( reportDesignerContext );
  }

  /**
   * Initialize component of the controller.
   */
  public void init() {

    // Listeners
    modelWrapperUpdateHandler = new ModelWrapperUpdateHandler();
    pathWrapperUpdateHandler = new PathWrapperUpdateHandler();
    modelWrapper.addPropertyChangeListener( modelWrapperUpdateHandler );
    pentahoPathWrapper.addPropertyChangeListener( pathWrapperUpdateHandler );

    // Report extensions
    initExtensionsMap( pentahoPathWrapper );

    // Server URL check box
    final JCheckBox includeServerUrl =
        drillDownUi.getComponent( SwingRemoteDrillDownUi.ComponentLookup.SERVER_URL_CHECKBOX );
    includeServerUrl.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed( ActionEvent e ) {
        pentahoPathWrapper.setUseRemoteServer( includeServerUrl.isSelected() );
      }
    } );

    // Server URL field
    JTextField serverUrlField =
        drillDownUi.getComponent( SwingRemoteDrillDownUi.ComponentLookup.SERVER_URL_FIELD );

    serverUrlField.getDocument().addDocumentListener( new DocumentBindingListener() {
      @Override
      protected void setData( String data ) {
        pentahoPathWrapper.setServerPath( data );
      }
    } );

    // Login button
    JButton loginButton =
        drillDownUi.getComponent( SwingRemoteDrillDownUi.ComponentLookup.LOGIN_BUTTON );
    loginButton.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed( ActionEvent e ) {
        final LoginTask loginTask = new LoginTask( reportDesignerContext, drillDownUi.getEditorPanel().getParent(), new LoginCompleteTask( null ) );
        SwingUtilities.invokeLater( loginTask );
      }
    } );

    // Path field
    JTextField pathField =
        drillDownUi.getComponent( SwingRemoteDrillDownUi.ComponentLookup.PATH_FIELD );
    pathField.getDocument().addDocumentListener( new DocumentBindingListener() {
      @Override
      protected void setData( String data ) {
        if ( data == null || data.isEmpty() ) {
          return;
        }

        DrillDownParameter parameterPath = new DrillDownParameter( "::pentaho-path", new String( "\"" + data + "\"" ) );

        DrillDownParameter[] currentParams = modelWrapper.getDrillDownParameter();
        ArrayList<DrillDownParameter> currentParamsList = new ArrayList<>( Arrays.asList( currentParams ) );

        Optional<DrillDownParameter> lookupItem = currentParamsList.stream().
                filter( a -> a.getName().equals( "::pentaho-path" ) ).findFirst();

        if ( lookupItem.isPresent() ) {
          lookupItem.get().setFormulaFragment( new String( "\"" + data + "\"" ) );
        } else {
          currentParamsList.add( parameterPath );
        }

        DrillDownParameter[] result = currentParamsList.toArray( new DrillDownParameter[currentParamsList.size()] );
        ( drillDownUi.<DrillDownParameterTable>getComponent(
                        SwingRemoteDrillDownUi.ComponentLookup.PARAMETER_TABLE
                ) ).setDrillDownParameter( result );
        }
    } );

    // Browse button
    JButton browseButton =
        drillDownUi.getComponent( SwingRemoteDrillDownUi.ComponentLookup.BROWSE_BUTTON );
    browseButton.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed( ActionEvent e ) {
        final ReportDocumentContext activeContext = reportDesignerContext.getActiveContext();
        if ( pentahoPathWrapper.getLoginData() == null ) {
          final String path = modelWrapper.getDrillDownPath();
          if ( path != null ) {
            final AuthenticationStore authStore = activeContext.getAuthenticationStore();
            final String username = authStore.getUsername( path );
            final String password = authStore.getPassword( path );
            final int timeout = authStore.getIntOption( path, "timeout", 0 );
            pentahoPathWrapper.setLoginData( new AuthenticationData( path, username, password, timeout ) );
          }
        }

        final LoginTask loginTask =
            new LoginTask( reportDesignerContext, drillDownUi.getEditorPanel().getParent(), new LoginCompleteTask(
                new SelectDrillTargetTask(
                    pentahoPathWrapper,
                    drillDownUi.getEditorPanel().getParent(),
                    new RefreshParameterTask(),
                    activeContext )
            ), pentahoPathWrapper.getLoginData() );
        SwingUtilities.invokeLater( loginTask );
      }
    } );

    // Parameter Table
    DrillDownParameterTable table =
        drillDownUi.getComponent( SwingRemoteDrillDownUi.ComponentLookup.PARAMETER_TABLE );
    table.addDrillDownParameterRefreshListener( new UpdateParametersHandler() );
    table.addPropertyChangeListener( DrillDownParameterTable.DRILL_DOWN_PARAMETER_PROPERTY, new TableModelBinding() );
    PentahoParameterRefreshHandler parameterRefreshHandler = new PentahoParameterRefreshHandler( pentahoPathWrapper, reportDesignerContext, drillDownUi.getEditorPanel() );
    table.addDrillDownParameterRefreshListener( parameterRefreshHandler );
    parameterRefreshHandler.setParameterTable( table );
    table.setReportDesignerContext( reportDesignerContext );


    pentahoPathWrapper.addPropertyChangeListener( PentahoPathModel.LOCAL_PATH_PROPERTY,
            new CheckEmptyPathHandler( table ) );

    SwingUtilities.invokeLater( new RefreshParameterTask() );

    // restore any parameters that might have been lost while initializing the UI.
    modelWrapper.setDrillDownParameter( modelWrapper.getDrillDownParameter() );

    pentahoPathWrapper.setLocalPathFromParameter( modelWrapper.getDrillDownParameter() );
    if ( StringUtils.isEmpty( modelWrapper.getDrillDownPath() ) ) {
      pentahoPathWrapper.setUseRemoteServer( false );
      final ReportDocumentContext reportRenderContext = reportDesignerContext.getActiveContext();
      if ( reportRenderContext != null ) {
        final Object o = reportRenderContext.getProperties().get( "pentaho-login-url" );
        if ( o != null ) {
          pentahoPathWrapper.setServerPath( String.valueOf( o ) );
        } else {
          pentahoPathWrapper.setServerPath( null );
        }
      } else {
        pentahoPathWrapper.setServerPath( null );
      }
    } else {
      pentahoPathWrapper.setUseRemoteServer( true );
      pentahoPathWrapper.setServerPath( modelWrapper.getDrillDownPath() );
    }

  }

  /**
   * Initialize file extensions map for the path wrapper.
   *
   * @param pentahoPathWrapper path wrapper.
   */
  private void initExtensionsMap( PentahoPathModel pentahoPathWrapper ) {
    final DrillDownProfile[] drillDownProfileByGroup =
            DrillDownProfileMetaData.getInstance().getDrillDownProfileByGroup( "pentaho-sugar" ); // pentaho-sugar
    for ( int i = 0; i < drillDownProfileByGroup.length; i++ ) {
      final DrillDownProfile profile = drillDownProfileByGroup[i];
      final String profileName = profile.getName();
      final String extension = profile.getAttribute( "extension" );
      final boolean noParameter = profileName.endsWith( "-no-parameter" );
      final boolean local = profileName.startsWith( "local-" );
      if ( StringUtils.isEmpty( extension ) ) {
        pentahoPathWrapper.registerExtension( null, local, noParameter, profileName );
      } else {
        pentahoPathWrapper.registerExtension( "." + extension, local, noParameter, profileName );
      }
    }
  }

  /**
   * Deactivate controller.
   */
  public void deactivate() {
    modelWrapper.removePropertyChangeListener( modelWrapperUpdateHandler );
    pentahoPathWrapper.removePropertyChangeListener( pathWrapperUpdateHandler );
  }

  /**
   * Service task to perform after BI-server authentication.
   */
  private class LoginCompleteTask implements AuthenticatedServerTask {
    private AuthenticationData loginData;
    private boolean storeUpdates;
    private AuthenticatedServerTask nextTask;

    private LoginCompleteTask( final AuthenticatedServerTask nextTask ) {
      this.nextTask = nextTask;
    }

    public void setLoginData( final AuthenticationData loginData, final boolean storeUpdates ) {
      this.loginData = loginData;
      this.storeUpdates = storeUpdates;
    }

    public void run() {
      pentahoPathWrapper.setLoginData( loginData );

      final ReportDocumentContext reportRenderContext = reportDesignerContext.getActiveContext();
      final Object o = reportRenderContext.getProperties().get( "pentaho-login-url" );
      if ( o == null ) {
        reportRenderContext.getProperties().put( "pentaho-login-url", loginData.getUrl() );
      }

      if ( nextTask != null ) {
        nextTask.setLoginData( loginData, storeUpdates );
        SwingUtilities.invokeLater( nextTask );
      }
    }
  }

  /**
   * PropertyChangeListener for model wrapper.
   */
  private class ModelWrapperUpdateHandler implements PropertyChangeListener {

    @Override
    public void propertyChange( PropertyChangeEvent evt ) {
      AuthenticationData loginData = pentahoPathWrapper.getLoginData();
      String serverUrl = loginData == null ? "" : loginData.getUrl();
      JTextField serverUrlField = drillDownUi.<JTextField>getComponent(
          SwingRemoteDrillDownUi.ComponentLookup.SERVER_URL_FIELD );
      if ( !ObjectUtils.equals( serverUrlField.getText(), serverUrl ) ) {
        serverUrlField.setText( serverUrl );
      }

      String path = pentahoPathWrapper.getLocalPath();
      JTextField pathField = drillDownUi.<JTextField>getComponent(
          SwingRemoteDrillDownUi.ComponentLookup.PATH_FIELD );
      if ( !ObjectUtils.equals( pathField.getText(), path ) ) {
        pathField.setText( path );
      }
    }
  }

  /**
   * PropertyChangeListener for drill down parameter table (empty check).
   */
  private class CheckEmptyPathHandler implements PropertyChangeListener {
    private DrillDownParameterTable paramTableElement;

    private CheckEmptyPathHandler( final DrillDownParameterTable paramTableElement ) {
      this.paramTableElement = paramTableElement;
      propertyChange( null );
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( StringUtils.isEmpty( pentahoPathWrapper.getLocalPath() ) ) {
        paramTableElement.setEnabled( false );
      } else {
        paramTableElement.setEnabled( true );
      }
    }
  }

  /**
   * PropertyChangeListener for path wrapper.
   */
  private class PathWrapperUpdateHandler implements PropertyChangeListener {
    private PathWrapperUpdateHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( PentahoPathModel.LOCAL_PATH_PROPERTY.equals( evt.getPropertyName() ) ) {
        modelWrapper.setDrillDownParameter( modelWrapper.getDrillDownParameter() );
        modelWrapper.setDrillDownConfig( pentahoPathWrapper.getDrillDownProfile() );
      } else if ( PentahoPathModel.USE_REMOTE_SERVER_PROPERTY.equals( evt.getPropertyName() ) ) {
        modelWrapper.setDrillDownConfig( pentahoPathWrapper.getDrillDownProfile() );
        if ( pentahoPathWrapper.isUseRemoteServer() == false ) {
          modelWrapper.setDrillDownPath( null );
        } else {
          modelWrapper.setDrillDownPath( pentahoPathWrapper.getServerPath() );
        }
      } else if ( PentahoPathModel.HIDE_PARAMETER_UI_PROPERTY.equals( evt.getPropertyName() ) ) {
        modelWrapper.setDrillDownConfig( pentahoPathWrapper.getDrillDownProfile() );
      } else if ( PentahoPathModel.LOGIN_DATA_PROPERTY.equals( evt.getPropertyName() ) ) {
        if ( pentahoPathWrapper.isUseRemoteServer() == false ) {
          modelWrapper.setDrillDownPath( null );
        } else {
          modelWrapper.setDrillDownPath( pentahoPathWrapper.getServerPath() );
        }
      }
    }
  }

  /**
   * PropertyChangeListener for drill down parameter table.
   */
  private class TableModelBinding implements PropertyChangeListener {

    /**
     * {@inheritDoc}
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      ArrayList<DrillDownParameter> filterParams = new ArrayList<>();
      DrillDownParameter[] unfilterParams = ( drillDownUi.<DrillDownParameterTable>getComponent(
              SwingRemoteDrillDownUi.ComponentLookup.PARAMETER_TABLE
      ) ).getDrillDownParameter();

      for ( DrillDownParameter param : unfilterParams ) {
        if ( param.getType() == DrillDownParameter.Type.SYSTEM && param.getFormulaFragment() != null
                && !param.getFormulaFragment().equals( "NA()" ) ) {
          filterParams.add( param );
        } else if ( param.getType() != DrillDownParameter.Type.SYSTEM ) {
          filterParams.add( param );
        }
      }

      modelWrapper.setDrillDownParameter( filterParams.toArray( new DrillDownParameter[filterParams.size()] ) );
    }
  }

  /**
   * ParameterRefreshListener for drill down parameter table.
   */
  private class UpdateParametersHandler implements DrillDownParameterRefreshListener {

    /**
     * {@inheritDoc}
     */
    public void requestParameterRefresh( final DrillDownParameterRefreshEvent event ) {
      final HashMap<String, DrillDownParameter> entries = new HashMap<String, DrillDownParameter>();
      final DrillDownParameter[] originalParams = event.getParameter();
      for ( int i = 0; i < originalParams.length; i++ ) {
        final DrillDownParameter param = originalParams[ i ];
        param.setType( DrillDownParameter.Type.MANUAL );
        entries.put( param.getName(), param );
      }

      final ReportDocumentContext activeContext = reportDesignerContext.getActiveContext();
      final MasterReport masterReportElement = activeContext.getContextRoot();
      final ReportParameterDefinition reportParams = masterReportElement.getParameterDefinition();
      final ParameterDefinitionEntry[] parameterDefinitionEntries = reportParams.getParameterDefinitions();

      for ( int i = 0; i < parameterDefinitionEntries.length; i++ ) {
        final ParameterDefinitionEntry entry = parameterDefinitionEntries[i];
        if ( entries.containsKey( entry.getName() ) == false ) {
          entries.put( entry.getName(),
                  new DrillDownParameter( entry.getName(), null, DrillDownParameter.Type.PREDEFINED, false, false ) );
        } else {
          final DrillDownParameter parameter = entries.get( entry.getName() );
          parameter.setType( DrillDownParameter.Type.PREDEFINED );
        }
      }

      final DrillDownParameter[] parameters = entries.values().toArray( new DrillDownParameter[ entries.size() ] );
      modelWrapper.setDrillDownParameter( parameters );
      ( drillDownUi.<DrillDownParameterTable>getComponent(
          SwingRemoteDrillDownUi.ComponentLookup.PARAMETER_TABLE
      ) ).setDrillDownParameter( parameters );
    }
  }

  /**
   * Service task to refresh drill down parameters table.
   */
  protected class RefreshParameterTask implements Runnable {
    public void run() {
      ( (DrillDownParameterTable) drillDownUi.getComponent( SwingRemoteDrillDownUi.ComponentLookup.PARAMETER_TABLE ) ).refreshParameterData();
    }
  }

}
