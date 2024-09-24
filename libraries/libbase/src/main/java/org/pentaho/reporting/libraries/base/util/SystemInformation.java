/*
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
* Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
* All rights reserved.
*/

package org.pentaho.reporting.libraries.base.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class SystemInformation {
  private static final Log logger = LogFactory.getLog( SystemInformation.class );

  public SystemInformation() {
  }

  public void logSystemInformation() {
    if ( logger.isInfoEnabled() ) {
      logger.info( getSystemInformation() );
    }
  }

  @SuppressWarnings( { "HardcodedLineSeparator" } )
  private String getSystemInformation() {
    final StringBuilder sb = new StringBuilder( 10000 );
    sb.append( "Current System Configuration\n" );
    sb.append( "----------------------------\n" );
    printMapAsTable( sb, new TreeMap<Object, Object>( System.getProperties() ) );
    sb.append( "\n" );
    sb.append( "\n" );

    //environment
    sb.append( "Current System Environment Variables\n" );
    sb.append( "------------------------------------\n" );
    printMapAsTable( sb, new TreeMap<Object, Object>( System.getenv() ) );
    sb.append( "\n" );
    sb.append( "\n" );

    //other
    sb.append( "Other Properties\n" );
    sb.append( "----------------\n" );//NON-NLS
    printMapAsTable( sb, getOtherProperties() );
    sb.append( "\n" );
    sb.append( "\n" );

    return sb.toString();
  }

  private void printMapAsTable( final StringBuilder sb, final Map environmentMap ) {
    for ( final Object key : environmentMap.keySet() ) {
      String value = (String) environmentMap.get( key );
      value = formatLongPathList( value );
      sb.append( String.format( "%1$-40s%2$-40s\n", key, value ) );
    }
  }

  private String formatLongPathList( String value ) {
    if ( value != null ) {
      value = value.replace( "\n", "\\n" );//NON-NLS
      value = value.replace( "\f", "\\f" );//NON-NLS
      value = value.replace( "\r", "\\r" );//NON-NLS
      if ( value.length() > 80 ) {
        value = value.replace( File.pathSeparator, File.pathSeparator +
          "\n                                        " );//NON-NLS
      }
    }
    return value;
  }


  public static Map<String, String> getOtherProperties() {
    final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
    map.put( "File.CurrentDirAbs", new File( "." ).getAbsolutePath() );//NON-NLS
    try {
      map.put( "File.CurrentDirCanonical", new File( "." ).getCanonicalPath() );//NON-NLS
    } catch ( IOException e ) {
      // ignore 
    }
    map.put( "UIManager.LookAndFeel", UIManager.getLookAndFeel().getClass().getName() );//NON-NLS
    map.put( "Toolkit", Toolkit.getDefaultToolkit().getClass().getName() );//NON-NLS
    if ( GraphicsEnvironment.isHeadless() == false ) {
      map.put( "Toolkit.MenuShortcutKeyMask", //NON-NLS
        String.valueOf( Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );//NON-NLS
      map.put( "Toolkit.ScreenResolution",
        String.valueOf( Toolkit.getDefaultToolkit().getScreenResolution() ) );//NON-NLS
      map.put( "Toolkit.ScreenSize", String.valueOf( Toolkit.getDefaultToolkit().getScreenSize() ) );//NON-NLS
    }
    map.put( "Runtime.availableProcessors", String.valueOf( Runtime.getRuntime().availableProcessors() ) );//NON-NLS
    map.put( "Runtime.maximumMemory", String.valueOf( Runtime.getRuntime().maxMemory() ) );//NON-NLS

    addDesktopProperty( map, "awt.mouse.numButtons" );//NON-NLS
    addDesktopProperty( map, "awt.multiClickInterval" );//NON-NLS
    addDesktopProperty( map, "DnD.Autoscroll.cursorHysteresis" );//NON-NLS
    addDesktopProperty( map, "DnD.Autoscroll.initialDelay" );//NON-NLS
    addDesktopProperty( map, "DnD.Autoscroll.interval" );//NON-NLS
    addDesktopProperty( map, "DnD.Cursor.CopyDrop" );//NON-NLS
    addDesktopProperty( map, "DnD.Cursor.CopyNoDrop" );//NON-NLS
    addDesktopProperty( map, "DnD.Cursor.LinkDrop" );//NON-NLS
    addDesktopProperty( map, "DnD.Cursor.LinkNoDrop" );//NON-NLS
    addDesktopProperty( map, "DnD.Cursor.MoveDrop" );//NON-NLS
    addDesktopProperty( map, "DnD.Cursor.MoveNoDrop" );//NON-NLS
    addDesktopProperty( map, "DnD.gestureMotionThreshold" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/CanChangeAccels" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/CursorThemeName" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/CursorThemeSize" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/FileChooserBackend" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/FontName" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/IMPreeditStyle" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/IMStatusStyle" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/KeyThemeName" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/MenuBarAccel" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/MenuImages" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/ShowInputMethodMenu" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/ShowUnicodeMenu" );//NON-NLS
    addDesktopProperty( map, "gnome.Gtk/ToolbarStyle" );//NON-NLS
    addDesktopProperty( map, "gnome.Net/CursorBlink" );//NON-NLS
    addDesktopProperty( map, "gnome.Net/CursorBlinkTime" );//NON-NLS
    addDesktopProperty( map, "gnome.Net/DndDragThreshold" );//NON-NLS
    addDesktopProperty( map, "gnome.Net/DoubleClickTime" );//NON-NLS
    addDesktopProperty( map, "gnome.Net/FallbackIconTheme" );//NON-NLS
    addDesktopProperty( map, "gnome.Net/IconThemeName" );//NON-NLS
    addDesktopProperty( map, "gnome.Net/ThemeName" );//NON-NLS
    addDesktopProperty( map, "gnome.Xft/Antialias" );//NON-NLS
    addDesktopProperty( map, "gnome.Xft/DPI" );//NON-NLS
    addDesktopProperty( map, "gnome.Xft/Hinting" );//NON-NLS
    addDesktopProperty( map, "gnome.Xft/HintStyle" );//NON-NLS
    addDesktopProperty( map, "gnome.Xft/RGBA" );//NON-NLS
    addDesktopProperty( map, "Shell.shellFolderManager" );//NON-NLS
    addDesktopProperty( map, "win.3d.backgroundColor" );//NON-NLS
    addDesktopProperty( map, "win.3d.darkShadowColor" );//NON-NLS
    addDesktopProperty( map, "win.3d.highlightColor" );//NON-NLS
    addDesktopProperty( map, "win.3d.lightColor" );//NON-NLS
    addDesktopProperty( map, "win.3d.shadowColor" );//NON-NLS
    addDesktopProperty( map, "win.ansiFixed.font" );//NON-NLS
    addDesktopProperty( map, "win.button.textColor" );//NON-NLS
    addDesktopProperty( map, "win.defaultGUI.font" );//NON-NLS
    addDesktopProperty( map, "win.frame.backgroundColor" );//NON-NLS
    addDesktopProperty( map, "win.frame.textColor" );//NON-NLS
    addDesktopProperty( map, "win.item.highlightColor" );//NON-NLS
    addDesktopProperty( map, "win.item.highlightTextColor" );//NON-NLS
    addDesktopProperty( map, "win.menu.backgroundColor" );//NON-NLS
    addDesktopProperty( map, "win.menubar.backgroundColor" );//NON-NLS
    addDesktopProperty( map, "win.menu.font" );//NON-NLS
    addDesktopProperty( map, "win.menu.keyboardCuesOn" );//NON-NLS
    addDesktopProperty( map, "win.menu.textColor" );//NON-NLS
    addDesktopProperty( map, "win.scrollbar.backgroundColor" );//NON-NLS
    addDesktopProperty( map, "win.scrollbar.width" );//NON-NLS
    addDesktopProperty( map, "win.text.grayedTextColor" );//NON-NLS
    addDesktopProperty( map, "win.xpstyle.colorName" );//NON-NLS
    addDesktopProperty( map, "win.xpstyle.dllName" );//NON-NLS
    addDesktopProperty( map, "win.xpstyle.sizeName" );//NON-NLS
    addDesktopProperty( map, "win.xpstyle.themeActive" );//NON-NLS
    return map;
  }


  private static void addDesktopProperty( final LinkedHashMap<String, String> map, final String key ) {
    try {
      final Object value = Toolkit.getDefaultToolkit().getDesktopProperty( key );
      if ( value != null ) {
        map.put( key, value.toString() );
      }
    } catch ( Throwable t ) {
      // AWT is a stupid thing and tries to die with a headless exception if certain properties
      // are queried in headless mode.
    }
  }
}
