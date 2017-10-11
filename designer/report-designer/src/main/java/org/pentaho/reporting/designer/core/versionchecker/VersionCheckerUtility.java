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

package org.pentaho.reporting.designer.core.versionchecker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.versionchecker.BasicVersionCheckerProvider;
import org.pentaho.versionchecker.IVersionCheckErrorHandler;
import org.pentaho.versionchecker.IVersionCheckResultHandler;
import org.pentaho.versionchecker.VersionChecker;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class VersionCheckerUtility {
  private static final Log logger = LogFactory.getLog( VersionCheckerUtility.class );

  private VersionCheckerUtility() {
  }

  public static void handlerVersionCheck( final Frame parent ) {
    if ( VersionCheckerUtility.isInitialCheck() ) {
      VersionCheckerUtility.setCheckVersion( VersionCheckerUtility.getUserApprovalForVersionChecker( parent ) );
    }
    if ( VersionCheckerUtility.getCheckVersion() ) {
      VersionCheckerUtility.checkVersion( parent, false, true );
    }
  }

  private static void setCheckVersion( final boolean checkVersion ) {
    WorkspaceSettings.getInstance().setUseVersionChecker( checkVersion );
  }

  private static boolean isInitialCheck() {
    return WorkspaceSettings.getInstance().isInitialVersionCheck();
  }

  private static boolean getCheckVersion() {
    return WorkspaceSettings.getInstance().isUseVersionChecker();
  }

  private static boolean getUserApprovalForVersionChecker( final Frame owner ) {
    final VersionCheckerEnableDialog dialog = new VersionCheckerEnableDialog( owner );
    return dialog.performEdit();
  }

  public static void checkVersion( final Component parent, final boolean forcePrompt, final boolean exitOnLaunch ) {
    final BasicVersionCheckerProvider dataProvider = new BasicVersionCheckerProvider( VersionCheckerUtility.class );
    final boolean gaOnly = !WorkspaceSettings.getInstance().isNotifyForAllBuilds();
    if ( gaOnly ) {
      dataProvider.setVersionRequestFlags( BasicVersionCheckerProvider.DEPTH_GA_MASK );
    } else {
      dataProvider.setVersionRequestFlags( BasicVersionCheckerProvider.DEPTH_ALL_MASK );
    }
    final VersionChecker vc = new VersionChecker();
    vc.setDataProvider( dataProvider );
    vc.addResultHandler( new InternalResultHandler( parent, forcePrompt, exitOnLaunch ) );
    vc.addErrorHandler( new NoPromptErrorHandler() );

    // start new thread; do not run in event thread
    final Thread vcThread = new Thread( new VersionCheckerRunnable( vc ) );
    vcThread.setDaemon( true );
    vcThread.start();
  }

  private static class VersionCheckerRunnable implements Runnable {
    private VersionChecker vc;

    public VersionCheckerRunnable( final VersionChecker vc ) {
      if ( vc == null ) {
        throw new NullPointerException();
      }
      this.vc = vc;
    }

    public void run() {
      vc.performCheck( false );
    }
  }

  private static class InternalResultHandler implements IVersionCheckResultHandler {
    private Component parent;
    private final boolean forcePrompt;
    private final boolean exitOnLaunch;

    protected InternalResultHandler( final Component parent, final boolean forcePrompt, final boolean exitOnLaunch ) {
      this.parent = parent;
      this.forcePrompt = forcePrompt;
      this.exitOnLaunch = exitOnLaunch;
    }

    public void processResults( final String result ) {
      try {
        final SAXReader reader = new SAXReader();
        final Document templateDoc = reader.read( new ByteArrayInputStream( result.getBytes() ) );
        final List<UpdateInfo> updates = new ArrayList<UpdateInfo>();
        final List updateElements = templateDoc.getRootElement().selectNodes( "/vercheck/product/update" );//NON-NLS
        for ( int i = 0; i < updateElements.size(); i++ ) {
          final Element updateElement = (Element) updateElements.get( i );
          final String version = updateElement.attributeValue( "version" );//NON-NLS
          final String type = updateElement.attributeValue( "type" );//NON-NLS
          //final String os = updateElement.attributeValue("os");
          final String downloadUrl = updateElement.selectSingleNode( "downloadurl" ).getText();//NON-NLS
          final UpdateInfo info = new UpdateInfo( version, type, downloadUrl );
          updates.add( info );
        }

        if ( updates.isEmpty() ) {
          if ( forcePrompt ) {
            JOptionPane.showMessageDialog( parent,
              "No update is available at this time.", "Version Update Info",
              JOptionPane.INFORMATION_MESSAGE );
          }
          return;
        }

        if ( ( forcePrompt ||
          !updates.get( updates.size() - 1 ).getVersion().equals(
            WorkspaceSettings.getInstance().getLastPromptedVersionUpdate() ) ) ) {
          final UpdateInfo[] updateInfos = updates.toArray( new UpdateInfo[ updates.size() ] );
          VersionConfirmationDialog.performUpdateAvailable( parent, updateInfos, exitOnLaunch );
        }
      } catch ( Exception e ) {
        // we cannot give errors
        logger.error( "The version checker encountered an error", e );
        JOptionPane.showMessageDialog( parent,
          "No update is available at this time.", "Version Update Info",
          JOptionPane.INFORMATION_MESSAGE );
      }
    }
  }

  private static class NoPromptErrorHandler implements IVersionCheckErrorHandler {
    public void handleException( final Exception e ) {
      // Disable the logging via the configuration. 
      logger.error( "The version checker encountered an error", e );
    }
  }
}
