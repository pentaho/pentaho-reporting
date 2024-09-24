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
