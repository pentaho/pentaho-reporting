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

package org.pentaho.reporting.designer.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.settings.ExternalToolSettings;
import org.pentaho.reporting.designer.core.settings.SettingsUtil;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * User: Martin Date: 01.03.2006 Time: 17:55:15
 */
public class ExternalToolLauncher {
  private static final Log logger = LogFactory.getLog( ExternalToolLauncher.class );
  private static final long timeout = 10000;

  private ExternalToolLauncher() {
  }


  public static void openURL( final String url ) throws IOException {
    //noinspection ConstantConditions
    if ( url == null ) {
      throw new IllegalArgumentException( "url must not be null" );
    }

    final ExternalToolSettings instance = ExternalToolSettings.getInstance();
    if ( instance.isUseDefaultBrowser() ) {
      try {
        if ( Desktop.isDesktopSupported()
            && Desktop.getDesktop().isSupported( Action.BROWSE ) ) {
          Desktop.getDesktop().browse( new URI( url ) );
        } else {
          logger.warn( UtilMessages.getInstance()
            .getString( "ExternalToolLauncher.unableToLaunchDefaultBrowser", url ) );
        }
      } catch ( Exception e ) {
        UncaughtExceptionsModel.getInstance().addException( e );
      }
    } else {
      if ( execute( instance.getCustomBrowserExecutable(),
        instance.getCustomBrowserParameters(), url ) == false ) {
        throw new IOException( UtilMessages.getInstance()
          .getString( "ExternalToolLauncher.errorMessage", instance.getCustomBrowserExecutable() ) );
      }
    }
  }


  public static void openPDF( final File file ) throws IOException {
    //noinspection ConstantConditions
    if ( file == null ) {
      throw new IllegalArgumentException( "file must not be null" );
    }

    final ExternalToolSettings toolSettings = ExternalToolSettings.getInstance();
    if ( toolSettings.isUseDefaultPDFViewer() ) {
      openDefaultViewer( file );
    } else {
      if ( execute
        ( toolSettings.getCustomPDFViewerExecutable(),
          toolSettings.getCustomPDFViewerParameters(),
          file.getCanonicalPath() ) == false ) {
        throw new IOException( UtilMessages.getInstance()
          .getString( "ExternalToolLauncher.errorMessage", toolSettings.getCustomPDFViewerExecutable() ) );
      }
    }
  }


  public static void openXLS( final File file ) throws IOException {
    //noinspection ConstantConditions
    if ( file == null ) {
      throw new IllegalArgumentException( "file must not be null" );
    }

    final ExternalToolSettings toolSettings = ExternalToolSettings.getInstance();
    if ( toolSettings.isUseDefaultXLSViewer() ) {
      try {
        openDefaultViewer( file );
      } catch ( Exception e ) {
        UncaughtExceptionsModel.getInstance().addException( e );
      }
    } else {
      if ( execute(
        toolSettings.getCustomXLSViewerExecutable(),
        toolSettings.getCustomXLSViewerParameters(),
        file.getCanonicalPath() ) == false ) {
        throw new IOException( UtilMessages.getInstance()
          .getString( "ExternalToolLauncher.errorMessage", toolSettings.getCustomXLSViewerExecutable() ) );
      }
    }
  }


  public static void openRTF( final File file ) throws IOException {
    //noinspection ConstantConditions
    if ( file == null ) {
      throw new IllegalArgumentException( "file must not be null" );
    }

    final ExternalToolSettings toolSettings = ExternalToolSettings.getInstance();
    if ( toolSettings.isUseDefaultRTFViewer() ) {
      openDefaultViewer( file );
    } else {
      if ( execute(
        toolSettings.getCustomRTFViewerExecutable(),
        toolSettings.getCustomRTFViewerParameters(),
        file.getCanonicalPath() ) == false ) {
        throw new IOException( UtilMessages.getInstance()
          .getString( "ExternalToolLauncher.errorMessage", toolSettings.getCustomRTFViewerExecutable() ) );
      }
    }
  }


  public static void openCSV( final File file ) throws IOException {
    //noinspection ConstantConditions
    if ( file == null ) {
      throw new IllegalArgumentException( "file must not be null" );
    }

    final ExternalToolSettings toolSettings = ExternalToolSettings.getInstance();
    if ( toolSettings.isUseDefaultCSVViewer() ) {
      openDefaultViewer( file );
    } else {
      if ( execute( toolSettings.getCustomCSVViewerExecutable(),
        toolSettings.getCustomCSVViewerParameters(),
        file.getCanonicalPath() ) == false ) {
        throw new IOException( UtilMessages.getInstance()
          .getString( "ExternalToolLauncher.errorMessage", toolSettings.getCustomCSVViewerExecutable() ) );
      }
    }
  }

  public static boolean execute( final String executable, final String parameters, final String file )
    throws IOException {
    // todo: Use a stream tokenizer (well, a custom one, as the builtin one messes up escaped quotes)
    // so that we can handle quoting gracefully ..
    boolean exitValue = false;
    final ArrayList<String> command = new ArrayList<String>();
    command.add( executable );
    for ( StringTokenizer tokenizer = new StringTokenizer( parameters ); tokenizer.hasMoreTokens(); ) {
      String s = tokenizer.nextToken();
      s = s.replace( "{0}", file );
      command.add( s );
    }

    final String osname = safeSystemGetProperty( "os.name", "<protected by system security>" ); // NON-NLS
    if ( StringUtils.startsWithIgnoreCase( osname, "Mac OS X" ) ) // NON-NLS
    {
      logger.debug( "Assuming Mac-OS X." ); // NON-NLS
      if ( executable.endsWith( ".app" ) || executable.endsWith( ".app/" ) ) // NON-NLS
      {
        command.add( 0, "-a" ); // NON-NLS
        command.add( 0, "/usr/bin/open" ); // NON-NLS
      }
    }

    final ProcessBuilder processBuilder = new ProcessBuilder( command.toArray( new String[ command.size() ] ) );
    Process process = null;
    ProcessWrapper processWrapper = null;
    try {
      process = processBuilder.start();
      processWrapper = new ProcessWrapper( process );
      processWrapper.start();
      processWrapper.join( timeout );
      if ( processWrapper.getfExitCode() != null ) {
        exitValue = processWrapper.getfExitCode() == 0;
      } else {
        // Set our exit code to 1
        exitValue = false;
        process.destroy();
      }
      //final Process p = process.start();
      //p.waitFor();
      //exitValue = p.exitValue() == 0;
      // 0 == normal; 2 == permissions issue, 3 == no rules found in mimeType
      logger.debug( "ProcessWrapper exitCode = " + processWrapper.getfExitCode() );
    } catch ( InterruptedException ie ) {
      processWrapper.interrupt();
      Thread.currentThread().interrupt();
      process.destroy();
      exitValue = false;
    } catch ( Exception e ) {
      logger.error( "Error in execute shell command " + command + " error: " + e.getMessage() );
      // process fails so redirect to openURL command to locate viewer
      exitValue = false;
    }
    return exitValue;
  }

  protected static String safeSystemGetProperty( final String name,
                                                 final String defaultValue ) {
    try {
      return System.getProperty( name, defaultValue );
    } catch ( final SecurityException se ) {
      // ignore exception
      return defaultValue;
    }
  }

  public static void openDefaultViewer( final File file ) throws IOException {

    final String osname = safeSystemGetProperty( "os.name", "<protected by system security>" ); // NON-NLS
    final String jrepath = safeSystemGetProperty( "java.home", "." ); // NON-NLS
    final String fs = safeSystemGetProperty( "file.separator", File.separator ); // NON-NLS

    logger.debug( "Running on operating system: " + osname ); // NON-NLS

    if ( StringUtils.startsWithIgnoreCase( osname, "windows" ) ) // NON-NLS
    {
      logger.debug( "Detected Windows." ); // NON-NLS
      if ( execute( "rundll32.exe", "SHELL32.DLL,ShellExec_RunDLL {0}", file.getAbsolutePath() ) == false ) // NON-NLS
      {
        openURL( file.toURI().toURL().toString() );
      }
    } else if ( StringUtils.startsWithIgnoreCase( osname, "Mac OS X" ) ) // NON-NLS
    {
      logger.debug( "Assuming Mac-OS X." ); // NON-NLS
      if ( execute( "/usr/bin/open", "{0}", file.getCanonicalPath() ) == false ) // NON-NLS
      {
        openURL( file.toURI().toURL().toString() );
      }
    } else if ( StringUtils.startsWithIgnoreCase( osname, "Linux" ) || // NON-NLS
      StringUtils.startsWithIgnoreCase( osname, "Solaris" ) || // NON-NLS
      StringUtils.startsWithIgnoreCase( osname, "HP-UX" ) || // NON-NLS
      StringUtils.startsWithIgnoreCase( osname, "AIX" ) || // NON-NLS
      StringUtils.startsWithIgnoreCase( osname, "SunOS" ) ) // NON-NLS

    {
      logger.debug( "Assuming unix." ); // NON-NLS
      final File mailcapExe = new File( "/usr/bin/run-mailcap" ); // NON-NLS
      if ( mailcapExe.exists() ) {
        logger.debug( "found /usr/bin/run=mailcap" );
        if ( execute( "/usr/bin/run-mailcap", "{0}", file.getCanonicalPath() ) == false ) // NON-NLS
        {
          openURL( file.toURI().toURL().toString() );
        }
      } else {
        // use our private version instead ...
        final File installDir = SettingsUtil.computeInstallationDirectory();
        if ( installDir == null ) {
          logger.debug( "Cannot determine installation directory; using browser as generic launcher." ); // NON-NLS
          openURL( file.toURI().toURL().toString() );
          return;
        }

        final File privateMailCapExe = new File( installDir, "resources/run-mailcap" ); // NON-NLS
        if ( privateMailCapExe.exists() ) {
          if ( execute( privateMailCapExe.getPath(), "{0}", file.getCanonicalPath() ) == false ) {
            openURL( file.toURI().toURL().toString() );
          }
        } else {
          logger.debug( "private copy of run-mailcap not found; using browser as generic launcher." ); // NON-NLS
          openURL( file.toURI().toURL().toString() );
          return;
        }
      }
    } else {
      logger.debug( "Not a known OS-Type; using browser as generic launcher." ); // NON-NLS
      openURL( file.toURI().toURL().toString() );
    }

  }

  static class ProcessWrapper extends Thread {

    private final Process fProcess;

    private Integer fExitCode;

    public Integer getfExitCode() {
      return fExitCode;
    }

    public ProcessWrapper( Process fProcess ) {
      this.fProcess = fProcess;
    }

    public void run() {
      try {
        fExitCode = fProcess.waitFor();
      } catch ( InterruptedException e ) {
        fExitCode = 0;
      } catch ( Exception ex ) {
        fExitCode = -1;
      }
    }
  }

}
