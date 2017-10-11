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
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;
import org.pentaho.ui.xul.XulEventSource;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

public class PentahoPathModel implements XulEventSource {

  private static class ExtensionMapping {
    private String extension;
    private String localMode;
    private String remoteMode;
    private String localNoParamMode;
    private String remoteNoParamMode;

    private ExtensionMapping( final String extension ) {
      this.extension = extension;
    }

    public String getExtension() {
      return extension;
    }

    public void set( final boolean local, final boolean noParameter, final String name ) {
      if ( local ) {
        if ( noParameter ) {
          localNoParamMode = name;
        } else {
          localMode = name;
        }
      } else {
        if ( noParameter ) {
          remoteNoParamMode = name;
        } else {
          remoteMode = name;
        }
      }

    }

    public String get( final boolean local, final boolean noParameter ) {
      if ( noParameter == true ) {
        if ( local ) {
          if ( localNoParamMode != null ) {
            return localNoParamMode;
          }
        } else {
          if ( remoteNoParamMode != null ) {
            return remoteNoParamMode;
          }
        }
      }

      if ( local ) {
        if ( localMode != null ) {
          return localMode;
        }
      } else {
        if ( remoteMode != null ) {
          return remoteMode;
        }
      }
      return localMode;
    }
  }

  private PropertyChangeSupport propertyChangeSupport;
  private String localPath;
  private AuthenticationData loginData;
  private HashMap<String, ExtensionMapping> extensionMap;
  private boolean useRemoteServer;
  private boolean hideParameterUi;
  private ReportDesignerContext reportDesignerContext;
  public static final String USE_REMOTE_SERVER_PROPERTY = "useRemoteServer";
  public static final String HIDE_PARAMETER_UI_PROPERTY = "hideParameterUi";
  public static final String SERVER_PATH_PROPERTY = "serverPath";
  public static final String LOGIN_DATA_PROPERTY = "loginData";
  public static final String LOCAL_PATH_PROPERTY = "localPath";

  public PentahoPathModel( final ReportDesignerContext reportDesignerContext ) {
    this.reportDesignerContext = reportDesignerContext;
    propertyChangeSupport = new PropertyChangeSupport( this );
    extensionMap = new HashMap<String, ExtensionMapping>();
  }

  public void registerExtension( final String extension, final boolean local, final boolean noParameter,
      final String profileName ) {
    ExtensionMapping mapping = extensionMap.get( extension );
    if ( mapping == null ) {
      mapping = new ExtensionMapping( extension );
      extensionMap.put( extension, mapping );
    }
    mapping.set( local, noParameter, profileName );
  }

  public AuthenticationData getLoginData() {
    return loginData;
  }

  public void setLoginData( final AuthenticationData loginData ) {
    final String oldServerPath = getServerPath();
    final AuthenticationData oldLoginData = this.loginData;
    this.loginData = loginData;
    this.propertyChangeSupport.firePropertyChange( LOGIN_DATA_PROPERTY, oldLoginData, loginData );
    this.propertyChangeSupport.firePropertyChange( SERVER_PATH_PROPERTY, oldServerPath, getServerPath() );
  }

  public String getServerPath() {
    if ( loginData == null ) {
      return null;
    }
    return loginData.getUrl();
  }

  public void setServerPath( final String serverPath ) {
    final String oldServerPath = getServerPath();
    if ( ObjectUtilities.equal( oldServerPath, serverPath ) ) {
      return;
    }

    if ( serverPath == null ) {
      setLoginData( null );
      propertyChangeSupport.firePropertyChange( SERVER_PATH_PROPERTY, oldServerPath, null );
      return;
    }

    AuthenticationData loginData = RepositoryLoginDialog.getStoredLoginData( serverPath, reportDesignerContext );
    if ( loginData == null ) {
      loginData = new AuthenticationData( serverPath );
    }
    setLoginData( loginData );
    propertyChangeSupport.firePropertyChange( SERVER_PATH_PROPERTY, oldServerPath, serverPath );
  }

  public boolean isUseRemoteServer() {
    return useRemoteServer;
  }

  public void setUseRemoteServer( final boolean useRemoteServer ) {
    final boolean oldUseRemoteServer = this.useRemoteServer;
    this.useRemoteServer = useRemoteServer;
    this.propertyChangeSupport.firePropertyChange( USE_REMOTE_SERVER_PROPERTY, oldUseRemoteServer, useRemoteServer );
  }

  public boolean isHideParameterUi() {
    return hideParameterUi;
  }

  public void setHideParameterUi( final boolean hideParameterUi ) {
    final boolean oldHide = this.hideParameterUi;
    this.hideParameterUi = hideParameterUi;
    this.propertyChangeSupport.firePropertyChange( HIDE_PARAMETER_UI_PROPERTY, oldHide, hideParameterUi );
  }

  public String getLocalPath() {
    return localPath;
  }

  public void setLocalPath( final String localPath ) {
    if ( ObjectUtilities.equal( localPath, this.localPath ) ) {
      return;
    }
    final String oldValue = this.localPath;
    this.localPath = localPath;
    this.propertyChangeSupport.firePropertyChange( LOCAL_PATH_PROPERTY, oldValue, localPath );
  }

  public void setLocalPathFromParameter( final DrillDownParameter[] params ) {
    String solution = null;
    String path = null;
    String name = null;
    String localPath = null;

    for ( int i = 0; i < params.length; i++ ) {
      final DrillDownParameter drillDownParameter = params[i];
      if ( "solution".equals( drillDownParameter.getName() ) ) { // NON-NLS
        solution = FormulaUtil.extractStaticTextFromFormulaFragment( drillDownParameter.getFormulaFragment() );
      } else if ( "path".equals( drillDownParameter.getName() ) ) { // NON-NLS
        path = FormulaUtil.extractStaticTextFromFormulaFragment( drillDownParameter.getFormulaFragment() );
      } else if ( "name".equals( drillDownParameter.getName() ) ) { // NON-NLS
        name = FormulaUtil.extractStaticTextFromFormulaFragment( drillDownParameter.getFormulaFragment() );
      } else if ( "::pentaho-path".equals( drillDownParameter.getName() ) ) { // NON-NLS
        localPath = FormulaUtil.extractStaticTextFromFormulaFragment( drillDownParameter.getFormulaFragment() );
      }
    }

    if ( StringUtils.isEmpty( localPath ) == false ) {
      setLocalPath( localPath );
    } else {
      final StringBuilder b = new StringBuilder();
      if ( StringUtils.isEmpty( solution ) == false ) {
        b.append( solution );
      }
      if ( StringUtils.isEmpty( path ) == false ) {
        b.append( "/" );
        b.append( path );
      }
      if ( StringUtils.isEmpty( name ) == false ) {
        b.append( "/" );
        b.append( name );
      }

      if ( b.length() == 0 ) {
        setLocalPath( null );
      } else {
        setLocalPath( b.toString() );
      }
    }
  }

  public String getPath() {
    if ( localPath == null ) {
      return null;
    }
    final String normalizedPath = localPath.replace( '\\', '/' );
    final String[] path = StringUtils.split( normalizedPath, "/" );
    final StringBuilder b = new StringBuilder();
    for ( int i = 1; i < path.length - 1; i++ ) {
      if ( i > 1 ) {
        b.append( '/' );
      }

      final String pathElement = path[i];
      b.append( pathElement );
    }
    return b.toString();
  }

  public String getName() {
    if ( localPath == null ) {
      return null;
    }
    final String normalizedPath = localPath.replace( '\\', '/' );
    final String[] path = StringUtils.split( normalizedPath, "/" );
    if ( path.length > 1 ) {
      return path[path.length - 1];
    }
    return null;
  }

  public String getSolution() {
    if ( localPath == null ) {
      return null;
    }
    final String normalizedPath = localPath.replace( '\\', '/' );
    final String[] path = StringUtils.split( normalizedPath, "/" );
    if ( path.length > 0 ) {
      return path[0];
    }
    return null;
  }

  public void addPropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( propertyName, listener );
  }

  public void removePropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( propertyName, listener );
  }

  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    this.propertyChangeSupport.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    this.propertyChangeSupport.removePropertyChangeListener( listener );
  }

  public String getDrillDownProfile() {
    final String name = getName();
    if ( name == null ) {
      return null;
    }

    final String extension = IOUtils.getInstance().getFileExtension( name );
    ExtensionMapping mapping = extensionMap.get( extension );
    if ( mapping == null ) {
      mapping = extensionMap.get( null );
      if ( mapping == null ) {
        return null;
      }
    }

    return mapping.get( isUseRemoteServer() == false, hideParameterUi );
  }

  public String[] getExtensions() {
    return extensionMap.keySet().toArray( new String[extensionMap.size()] );
  }

  /**
   * @return PropertyChangeSupport
   * 
   *         added to facilitate unit testing
   */
  PropertyChangeSupport getChangeListeners() {
    return propertyChangeSupport;
  }

}
