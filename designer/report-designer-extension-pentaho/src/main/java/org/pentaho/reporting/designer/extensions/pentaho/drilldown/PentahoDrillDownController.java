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

package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterTable;
import org.pentaho.reporting.designer.core.editor.drilldown.basic.DefaultXulDrillDownController;
import org.pentaho.reporting.designer.core.editor.drilldown.basic.XulDrillDownParameterTable;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.designer.extensions.pentaho.repository.actions.AuthenticatedServerTask;
import org.pentaho.reporting.designer.extensions.pentaho.repository.actions.LoginTask;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfileMetaData;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.DefaultBindingFactory;
import org.pentaho.ui.xul.dom.Document;

import java.awt.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingUtilities;

public abstract class PentahoDrillDownController extends DefaultXulDrillDownController {
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

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
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

  private class PentahoWrapperUpdateHandler implements PropertyChangeListener {
    private PentahoWrapperUpdateHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt
     *          A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( PentahoPathModel.LOCAL_PATH_PROPERTY.equals( evt.getPropertyName() ) ) {
        getWrapper().setDrillDownParameter( filterParameter( getWrapper().getDrillDownParameter() ) );
        getWrapper().setDrillDownConfig( pentahoPathWrapper.getDrillDownProfile() );
      } else if ( PentahoPathModel.USE_REMOTE_SERVER_PROPERTY.equals( evt.getPropertyName() ) ) {
        getWrapper().setDrillDownConfig( pentahoPathWrapper.getDrillDownProfile() );
        if ( pentahoPathWrapper.isUseRemoteServer() == false ) {
          getWrapper().setDrillDownPath( null );
        } else {
          getWrapper().setDrillDownPath( pentahoPathWrapper.getServerPath() );
        }
      } else if ( PentahoPathModel.HIDE_PARAMETER_UI_PROPERTY.equals( evt.getPropertyName() ) ) {
        getWrapper().setDrillDownConfig( pentahoPathWrapper.getDrillDownProfile() );
      } else if ( PentahoPathModel.LOGIN_DATA_PROPERTY.equals( evt.getPropertyName() ) ) {
        if ( pentahoPathWrapper.isUseRemoteServer() == false ) {
          getWrapper().setDrillDownPath( null );
        } else {
          getWrapper().setDrillDownPath( pentahoPathWrapper.getServerPath() );
        }
      }
    }
  }

  private class CheckEmptyPathHandler implements PropertyChangeListener {
    private XulComponent paramTableElement;

    private CheckEmptyPathHandler( final XulComponent paramTableElement ) {
      this.paramTableElement = paramTableElement;
      propertyChange( null );
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt
     *          A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( StringUtils.isEmpty( pentahoPathWrapper.getLocalPath() ) ) {
        paramTableElement.setDisabled( true );
      } else {
        paramTableElement.setDisabled( false );
      }
    }
  }

  private PentahoPathModel pentahoPathWrapper;
  private PentahoParameterRefreshHandler parameterRefreshHandler;
  private ReportDesignerContext reportDesignerContext;

  protected PentahoDrillDownController() {
  }

  protected PentahoPathModel getPentahoPathWrapper() {
    return pentahoPathWrapper;
  }

  protected abstract String getProfileName();

  public void
    init( final ReportDesignerContext reportDesignerContext, final DrillDownModel model, final String[] fields ) {
    this.reportDesignerContext = reportDesignerContext;
    super.init( reportDesignerContext, model, fields );

    final DrillDownParameter[] drillDownParameter = model.getDrillDownParameter();

    pentahoPathWrapper = new PentahoPathModel( reportDesignerContext );
    pentahoPathWrapper.addPropertyChangeListener( new PentahoWrapperUpdateHandler() );

    final DrillDownProfile[] drillDownProfileByGroup =
        DrillDownProfileMetaData.getInstance().getDrillDownProfileByGroup( getProfileName() );
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

    final Component c;
    final Object context = getXulDomContainer().getOuterContext();
    if ( context instanceof Component ) {
      c = (Component) context;
    } else {
      c = getReportDesignerContext().getView().getParent();
    }
    parameterRefreshHandler = new PentahoParameterRefreshHandler( pentahoPathWrapper, reportDesignerContext, c );

    final Document doc = getXulDomContainer().getDocumentRoot();
    final DefaultBindingFactory bindingFactory = new DefaultBindingFactory();
    bindingFactory.setDocument( doc );
    bindingFactory.setBindingType( Binding.Type.BI_DIRECTIONAL );
    final XulComponent configElement = doc.getElementById( "local-path" );
    if ( configElement != null ) {
      bindingFactory.createBinding( pentahoPathWrapper, PentahoPathModel.LOCAL_PATH_PROPERTY, "local-path", "value" );
    }

    final XulComponent localServerElement = doc.getElementById( "local-server-used" );
    if ( localServerElement != null ) {
      bindingFactory.createBinding( pentahoPathWrapper, PentahoPathModel.USE_REMOTE_SERVER_PROPERTY,
          "local-server-used", "checked" );
    }

    final XulComponent hideParameterUiElement = doc.getElementById( "parameter-table" );
    if ( hideParameterUiElement != null ) {
      bindingFactory.createBinding( pentahoPathWrapper, PentahoPathModel.HIDE_PARAMETER_UI_PROPERTY, "parameter-table",
          "hideParameterUi" );
    }

    final XulComponent serverElement = doc.getElementById( "server-login" );
    if ( serverElement != null ) {
      bindingFactory.createBinding( pentahoPathWrapper, PentahoPathModel.SERVER_PATH_PROPERTY, "server-login", "value" );
    }

    final DrillDownParameterTable drillDownParameterTable = getTable();
    if ( drillDownParameterTable != null ) {
      drillDownParameterTable.setFilteredParameterNames( new String[] { "solution", "path", "name" } );
      drillDownParameterTable.addDrillDownParameterRefreshListener( parameterRefreshHandler );
      parameterRefreshHandler.setParameterTable( drillDownParameterTable );
    }

    // restore any parameters that might have been lost while initializing the UI.
    model.setDrillDownParameter( drillDownParameter );

    pentahoPathWrapper.setLocalPathFromParameter( model.getDrillDownParameter() );
    if ( StringUtils.isEmpty( model.getDrillDownPath() ) ) {
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
      pentahoPathWrapper.setServerPath( model.getDrillDownPath() );
    }

    configureDisableTableOnEmptyFile();
  }

  protected void configureDisableTableOnEmptyFile() {
    final Document doc = getXulDomContainer().getDocumentRoot();
    final XulComponent paramTableElement = doc.getElementById( "parameter-table" ); // NON-NLS
    if ( paramTableElement instanceof XulDrillDownParameterTable == false ) {
      return;
    }

    pentahoPathWrapper.addPropertyChangeListener( PentahoPathModel.LOCAL_PATH_PROPERTY, new CheckEmptyPathHandler(
        paramTableElement ) );
  }

  /**
   * @noinspection UnusedDeclaration
   */
  public void login() {
    final Component c;
    final Object context = getXulDomContainer().getOuterContext();
    if ( context instanceof Component ) {
      c = (Component) context;
    } else {
      c = getReportDesignerContext().getView().getParent();
    }
    final LoginTask loginTask = new LoginTask( getReportDesignerContext(), c, new LoginCompleteTask( null ) );
    SwingUtilities.invokeLater( loginTask );
  }

  /**
   * @noinspection UnusedDeclaration
   */
  public void browse() {
    final ReportDocumentContext activeContext = getReportDesignerContext().getActiveContext();
    if ( pentahoPathWrapper.getLoginData() == null ) {
      final String path = getModel().getDrillDownPath();
      if ( path != null ) {
        final AuthenticationStore authStore = activeContext.getAuthenticationStore();
        final String username = authStore.getUsername( path );
        final String password = authStore.getPassword( path );
        final int timeout = authStore.getIntOption( path, "timeout", 0 );
        pentahoPathWrapper.setLoginData( new AuthenticationData( path, username, password, timeout ) );
      }
    }

    final Component c;
    final Object context = getXulDomContainer().getOuterContext();
    if ( context instanceof Component ) {
      c = (Component) context;
    } else {
      c = getReportDesignerContext().getView().getParent();
    }
    final LoginTask loginTask =
        new LoginTask( getReportDesignerContext(), c, new LoginCompleteTask( new SelectDrillTargetTask(
            pentahoPathWrapper, c, new RefreshParameterTask(), activeContext ) ), pentahoPathWrapper.getLoginData() );
    SwingUtilities.invokeLater( loginTask );
  }

  public void deactivate() {

  }
}
