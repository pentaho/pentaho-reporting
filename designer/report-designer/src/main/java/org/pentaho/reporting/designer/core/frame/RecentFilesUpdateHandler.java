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
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.frame;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerView;
import org.pentaho.reporting.designer.core.actions.global.OpenRecentReportAction;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.xul.ActionSwingMenuitem;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.components.XulMenuitem;
import org.pentaho.ui.xul.containers.XulMenupopup;
import org.pentaho.ui.xul.swing.tags.SwingMenuseparator;

import java.io.File;
import java.util.List;

public class RecentFilesUpdateHandler implements SettingsListener {
  private final ReportDesignerView xulDesignerFrame;
  private ReportDesignerContext context;
  private XulMenupopup reopenMenu;
  private XulMenuitem clearMenu;

  public RecentFilesUpdateHandler( final ReportDesignerContext context,
                                   final XulMenupopup reopenMenu,
                                   final XulMenuitem clearMenu ) {
    this.context = context;
    this.xulDesignerFrame = context.getView();
    this.reopenMenu = reopenMenu;
    this.clearMenu = clearMenu;
  }

  public void settingsChanged() {
    final File[] recentFiles = context.getRecentFilesModel().getRecentFiles();
    final List<XulComponent> xulComponents = reopenMenu.getChildNodes();
    final XulComponent[] objects = xulComponents.toArray( new XulComponent[ xulComponents.size() ] );
    for ( int i = 0; i < objects.length; i++ ) {
      final XulComponent object = objects[ i ];
      reopenMenu.removeChild( object );
    }

    if ( recentFiles.length == 0 ) {
      clearMenu.setDisabled( true );
    } else {
      clearMenu.setDisabled( false );
      for ( int i = 0; i < recentFiles.length; i++ ) {
        final File file = recentFiles[ i ];
        if ( file.exists() == false ) {
          continue;
        }

        final OpenRecentReportAction action = new OpenRecentReportAction( file );
        final ActionSwingMenuitem actionSwingMenuitem = xulDesignerFrame.createMenuItem( action );
        actionSwingMenuitem.setReportDesignerContext( context );
        reopenMenu.addChild( actionSwingMenuitem );
      }
      reopenMenu.addChild( new SwingMenuseparator( null, null, null, null ) );
    }
  }
}
