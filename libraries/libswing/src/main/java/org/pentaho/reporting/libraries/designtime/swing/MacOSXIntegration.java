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

package org.pentaho.reporting.libraries.designtime.swing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

public class MacOSXIntegration {
  private static final Log logger = LogFactory.getLog( MacOSXIntegration.class );
  public static final boolean MAC_OS_X;

  public static class ApplicationEventSupport {
    private Object event;
    private boolean handled;

    public ApplicationEventSupport( final Object event ) {
      this.event = event;
    }

    public String getFileName() {
      if ( event == null ) {
        return null;
      }
      try {
        final Method isHandledMethod = event.getClass().getDeclaredMethod( "getFilename" );
        // If the target method returns a boolean, use that as a hint
        final Object o = isHandledMethod.invoke( event );
        if ( o == null ) {
          return null;
        }
        return String.valueOf( o );
      } catch ( Exception ex ) {
        logger.debug( "OSXAdapter was unable to handle an ApplicationEvent: " + event, ex );
      }
      return null;
    }

    public boolean isHandled() {
      if ( event == null ) {
        return handled;
      }
      try {
        final Method isHandledMethod = event.getClass().getDeclaredMethod( "isHandled" );
        // If the target method returns a boolean, use that as a hint
        final Object o = isHandledMethod.invoke( event );
        return Boolean.TRUE.equals( o );
      } catch ( Exception ex ) {
        logger.debug( "OSXAdapter was unable to handle an ApplicationEvent: " + event, ex );
      }
      return false;
    }

    public void setHandled( final boolean handled ) {
      if ( event == null ) {
        this.handled = handled;
        return;
      }

      try {
        final Method setHandledMethod = event.getClass().getDeclaredMethod( "setHandled", boolean.class );
        // If the target method returns a boolean, use that as a hint
        setHandledMethod.invoke( event, handled );
      } catch ( Exception ex ) {
        logger.debug( "OSXAdapter was unable to handle an ApplicationEvent: " + event, ex );
      }
    }
  }

  private static class MacOSApplicationListener implements InvocationHandler {
    private Object application;

    private MacOSApplicationListener() throws Exception {
      final Class applicationClass = Class.forName( "com.apple.eawt.Application" );
      final Method getApplicationMethod = applicationClass.getDeclaredMethod( "getApplication" );
      // If the target method returns a boolean, use that as a hint
      application = getApplicationMethod.invoke( null );
    }

    public Object invoke( final Object proxy, final Method method, final Object[] args ) throws Throwable {
      logger.info( "Receiving event from Apple-System: " + method );
      if ( isMatch( "handleQuit", method, args ) ) {
        if ( handleAction( args[ 0 ], quitAction, "handleQuit" ) ) {
          initialized = false;
        }
      } else if ( isMatch( "handleReOpenApplication", method, args ) ) {
        handleAction( args[ 0 ], reOpenApplicationAction, "handleReOpenApplication" );
      } else if ( isMatch( "handleAbout", method, args ) ) {
        handleAction( args[ 0 ], aboutAction, "handleAbout" );
      } else if ( isMatch( "handleOpenApplication", method, args ) ) {
        handleAction( args[ 0 ], openApplicationAction, "handleOpenApplication" );
      } else if ( isMatch( "handleOpenFile", method, args ) ) {
        if ( handleAction( args[ 0 ], openFileAction, "handleOpenFile" ) == false ) {
          final ApplicationEventSupport eventSupport = new ApplicationEventSupport( args );
          if ( initialized == false && queueOpenFiles.size() < 100 ) {
            queueOpenFiles.add( eventSupport.getFileName() );
          }
        }
      } else if ( isMatch( "handlePreferences", method, args ) ) {
        handleAction( args[ 0 ], preferencesAction, "handlePreferences" );
      } else if ( isMatch( "handlePrintFile", method, args ) ) {
        if ( handleAction( args[ 0 ], printFileAction, "handlePrintFile" ) == false ) {
          final ApplicationEventSupport eventSupport = new ApplicationEventSupport( args );
          if ( initialized == false && queuePrintedFiles.size() < 100 ) {
            queuePrintedFiles.add( eventSupport.getFileName() );
          }
        }
      }
      return null;
    }

    private boolean handleAction( final Object args, final Action action, final String method ) {
      final ApplicationEventSupport eventSupport = new ApplicationEventSupport( args );
      if ( action == null ) {
        eventSupport.setHandled( false );
        return false;
      }

      final ConsumableActionEvent actionEvent = new ConsumableActionEvent( eventSupport, 0, method );
      action.actionPerformed( actionEvent );
      if ( actionEvent.isConsumed() ) {
        eventSupport.setHandled( true );
        return true;
      } else {
        eventSupport.setHandled( false );
        return false;
      }
    }

    private boolean isMatch( final String proxySignature, final Method method, final Object[] args ) {
      return ( proxySignature.equals( method.getName() ) && args.length == 1 );
    }
  }

  static {
    boolean result;
    try {
      result = ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac os x" ) );
    } catch ( Exception e ) {
      // ignore all errors.
      result = false;
    }
    MAC_OS_X = result;
  }

  private static MacOSApplicationListener integration;
  private static Action preferencesAction;
  private static Action openFileAction;
  private static Action openApplicationAction;
  private static Action reOpenApplicationAction;
  private static Action printFileAction;
  private static Action aboutAction;
  private static Action quitAction;
  private static ArrayList queueOpenFiles;
  private static ArrayList queuePrintedFiles;
  private static boolean initialized;

  protected MacOSXIntegration() {
  }

  private static void init() {
    if ( MAC_OS_X == false ) {
      return;
    }

    try {
      if ( integration == null ) {
        logger.warn( "Installing MacOS integration support." );
        integration = new MacOSApplicationListener();
        queueOpenFiles = new ArrayList();
        queuePrintedFiles = new ArrayList();
      }
    } catch ( Throwable t ) {
      logger.warn( "Unable to install the Mac-OS support.", t );
    }
  }

  public static Action getPreferencesAction() {
    return preferencesAction;
  }

  public static void setPreferencesAction( final Action preferencesAction ) {
    MacOSXIntegration.preferencesAction = preferencesAction;
    init();
  }

  public static Action getOpenFileAction() {
    return openFileAction;
  }

  public static void setOpenFileAction( final Action openFileAction ) {
    MacOSXIntegration.openFileAction = openFileAction;
    init();
    if ( queueOpenFiles.isEmpty() == false ) {
      final String[] fileNames = (String[]) queueOpenFiles.toArray( new String[ queueOpenFiles.size() ] );
      for ( int i = 0; i < fileNames.length; i++ ) {
        final String fileName = fileNames[ i ];
        logger.warn( "Would like to work with File: " + fileName );
      }
      queueOpenFiles.clear();
    }
  }

  public static Action getOpenApplicationAction() {
    return openApplicationAction;
  }

  public static void setOpenApplicationAction( final Action openApplicationAction ) {
    MacOSXIntegration.openApplicationAction = openApplicationAction;
    init();
  }

  public static Action getReOpenApplicationAction() {
    return reOpenApplicationAction;
  }

  public static void setReOpenApplicationAction( final Action reOpenApplicationAction ) {
    MacOSXIntegration.reOpenApplicationAction = reOpenApplicationAction;
    init();
  }

  public static Action getPrintFileAction() {
    return printFileAction;
  }

  public static void setPrintFileAction( final Action printFileAction ) {
    MacOSXIntegration.printFileAction = printFileAction;
    init();
    if ( queuePrintedFiles.isEmpty() == false ) {
      final String[] fileNames = (String[]) queuePrintedFiles.toArray( new String[ queuePrintedFiles.size() ] );
      for ( int i = 0; i < fileNames.length; i++ ) {
        final String fileName = fileNames[ i ];
        logger.warn( "Would like to print File: " + fileName );
      }
      queuePrintedFiles.clear();

    }
  }

  public static Action getAboutAction() {
    return aboutAction;
  }

  public static void setAboutAction( final Action aboutAction ) {
    MacOSXIntegration.aboutAction = aboutAction;
    init();
  }

  public static Action getQuitAction() {
    return quitAction;
  }

  public static void setQuitAction( final Action quitAction ) {
    MacOSXIntegration.quitAction = quitAction;
    init();
  }
}
