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


package org.pentaho.reporting.designer.core.xul;

import org.pentaho.reporting.designer.core.DesignerContextComponent;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.ReportDesignerUiPlugin;
import org.pentaho.reporting.designer.core.ReportDesignerUiPluginRegistry;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.XulLoader;
import org.pentaho.ui.xul.containers.XulMenu;
import org.pentaho.ui.xul.containers.XulMenupopup;
import org.pentaho.ui.xul.containers.XulWindow;
import org.pentaho.ui.xul.dom.Document;
import org.pentaho.ui.xul.impl.XulEventHandler;

import javax.swing.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XulDesignerFrame {
  private static final String DIALOG_DEFINITION_FILE = "org/pentaho/reporting/designer/core/xul/designer-frame.xul";
    //$NON-NLS-1$
  private XulWindow window;
  private ReportDesignerContext reportDesignerContext;

  public XulDesignerFrame() throws XulException {
    final ActionSwingXulLoader loader = new ActionSwingXulLoader();

    final ReportDesignerUiPlugin[] plugins = ReportDesignerUiPluginRegistry.getInstance().getPlugins();
    for ( int i = 0; i < plugins.length; i++ ) {
      final ReportDesignerUiPlugin plugin = plugins[ i ];
      final Map<String, String> map = plugin.getXulAdditionalHandlers();
      final Iterator<Map.Entry<String, String>> entryIterator = map.entrySet().iterator();
      while ( entryIterator.hasNext() ) {
        final Map.Entry<String, String> entry = entryIterator.next();
        loader.register( entry.getKey(), entry.getValue() );
      }
    }

    final XulDomContainer container = loader.loadXul( DIALOG_DEFINITION_FILE );
    final Document documentRoot = container.getDocumentRoot();

    for ( int i = 0; i < plugins.length; i++ ) {
      final ReportDesignerUiPlugin plugin = plugins[ i ];
      final String[] strings = plugin.getOverlaySources();
      for ( int j = 0; j < strings.length; j++ ) {
        final String source = strings[ j ];
        documentRoot.addOverlay( source );
      }
    }
    for ( int i = 0; i < plugins.length; i++ ) {
      final ReportDesignerUiPlugin plugin = plugins[ i ];
      final XulEventHandler[] xulEventHandlers = plugin.createEventHandlers();
      for ( int j = 0; j < xulEventHandlers.length; j++ ) {
        final XulEventHandler eventHandler = xulEventHandlers[ j ];
        container.addEventHandler( eventHandler );
      }
    }

    container.initialize();

    final XulComponent root = documentRoot.getRootElement();
    if ( root instanceof XulWindow ) {
      window = (XulWindow) root;
    } else {
      throw new XulException( "Error getting Xul Database Dialog root, element of type: " + root );
    }

  }

  public XulWindow getWindow() {
    return window;
  }

  public JMenuBar getMenuBar() {
    return getComponent( "main-menubar", JMenuBar.class );//NON-NLS
  }

  public <T extends XulComponent> T getXulComponent( String id, Class<T> type ) {
    final XulComponent mainMenuBar = window.getElementById( id );
    if ( mainMenuBar == null ) {
      return null;
    }
    if ( type.isInstance( mainMenuBar ) ) {
      return (T) mainMenuBar;
    }
    return null;
  }

  public <T extends JComponent> T getComponent( String id, Class<T> type ) {
    final XulComponent mainMenuBar = window.getElementById( id );
    if ( mainMenuBar == null ) {
      return null;
    }
    final Object o = mainMenuBar.getManagedObject();
    if ( type.isInstance( o ) ) {
      return (T) o;
    }
    return null;
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    this.reportDesignerContext = reportDesignerContext;
    installContext( window );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  private void installContext( final XulComponent component ) {
    final List<XulComponent> xulComponents = component.getChildNodes();
    for ( int i = 0; i < xulComponents.size(); i++ ) {
      final XulComponent child = xulComponents.get( i );
      if ( child instanceof DesignerContextComponent ) {
        final DesignerContextComponent asm = (DesignerContextComponent) child;
        asm.setReportDesignerContext( reportDesignerContext );
      }
      installContext( child );
    }
  }

  public ActionSwingMenuitem createMenu( final Action action ) {
    final ActionSwingMenuitem item = new ActionSwingMenuitem( null, null, null, "menu-item" );//NON-NLS
    item.setAction( action );
    return item;
  }

  public XulMenupopup createPopupMenu( final String label, final XulComponent parent ) throws XulException {

    final XulLoader xulLoader = window.getXulDomContainer().getXulLoader();
    final XulMenu menu = (XulMenu) xulLoader.createElement( "MENU" );
    menu.setLabel( label );
    parent.addChild( menu );

    final XulMenupopup childPopup = (XulMenupopup) xulLoader.createElement( "MENUPOPUP" );
    menu.addChild( childPopup );
    return childPopup;
  }
}
