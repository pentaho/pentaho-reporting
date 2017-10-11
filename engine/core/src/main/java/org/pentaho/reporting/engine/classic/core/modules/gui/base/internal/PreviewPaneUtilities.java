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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.internal;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.actions.ControlAction;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.actions.ControlActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.actions.ExportAction;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.actions.ZoomAction;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.actions.ZoomListActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.DefaultIconTheme;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.IconTheme;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ActionFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ActionPluginMenuComparator;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.DefaultActionFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExportActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingCommonModule;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

/**
 * Creation-Date: 17.11.2006, 15:06:51
 *
 * @author Thomas Morgner
 */
public class PreviewPaneUtilities {
  private static final String ICON_THEME_CONFIG_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.gui.common.IconTheme"; //$NON-NLS-1$
  private static final String ACTION_FACTORY_CONFIG_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.ActionFactory"; //$NON-NLS-1$
  private static final String CATEGORY_PREFIX = "org.pentaho.reporting.engine.classic.core.modules.gui.swing.category.";
  //$NON-NLS-1$
  private static final ZoomAction[] EMPTY_ZOOM_ACTION = new ZoomAction[0];

  private PreviewPaneUtilities() {
  }

  public static JMenu createMenu( final ActionCategory cat ) {
    final JMenu menu = new JMenu();
    menu.setText( cat.getDisplayName() );
    final Integer mnemonicKey = cat.getMnemonicKey();
    if ( mnemonicKey != null ) {
      menu.setMnemonic( mnemonicKey.intValue() );
    }
    final String toolTip = cat.getShortDescription();
    if ( toolTip != null && toolTip.length() > 0 ) {
      menu.setToolTipText( toolTip );
    }
    return menu;
  }

  public static ZoomAction[] buildMenu( final JMenu menu, final ActionPlugin[] actions, final PreviewPane pane ) {
    if ( actions.length == 0 ) {
      return EMPTY_ZOOM_ACTION;
    }
    ZoomAction[] retval = EMPTY_ZOOM_ACTION;
    Arrays.sort( actions, new ActionPluginMenuComparator() );
    boolean separatorPending = false;
    int count = 0;
    for ( int i = 0; i < actions.length; i++ ) {
      final ActionPlugin actionPlugin = actions[i];
      if ( actionPlugin.isAddToMenu() == false ) {
        continue;
      }

      if ( count > 0 && separatorPending ) {
        menu.addSeparator();
        separatorPending = false;
      }

      if ( actionPlugin instanceof ExportActionPlugin ) {
        final ExportActionPlugin exportPlugin = (ExportActionPlugin) actionPlugin;
        final ExportAction action = new ExportAction( exportPlugin, pane );
        menu.add( new JMenuItem( action ) );
        count += 1;
      } else if ( actionPlugin instanceof ControlActionPlugin ) {
        final ControlActionPlugin controlPlugin = (ControlActionPlugin) actionPlugin;
        final ControlAction action = new ControlAction( controlPlugin, pane );
        menu.add( new JMenuItem( action ) );
        count += 1;
      } else if ( actionPlugin instanceof ZoomListActionPlugin ) {
        retval = buildViewMenu( menu, pane );
      }

      if ( actionPlugin.isSeparated() ) {
        separatorPending = true;
      }

    }
    return retval;
  }

  private static ZoomAction[] buildViewMenu( final JMenu zoom, final PreviewPane pane ) {
    final double[] zoomFactors = pane.getZoomFactors();
    final ZoomAction[] zoomActions = new ZoomAction[zoomFactors.length];
    for ( int i = 0; i < zoomFactors.length; i++ ) {
      final double factor = zoomFactors[i];
      zoomActions[i] = new ZoomAction( factor, pane );
      zoom.add( new JMenuItem( zoomActions[i] ) );
    }
    return zoomActions;
  }

  public static void addActionsToToolBar( final JToolBar toolBar, final ActionPlugin[] reportActions,
      final JComboBox zoomSelector, final PreviewPane pane ) {
    if ( reportActions == null ) {
      return;
    }

    boolean separatorPending = false;
    int count = 0;
    for ( int i = 0; i < reportActions.length; i++ ) {
      final ActionPlugin actionPlugin = reportActions[i];
      if ( actionPlugin.isAddToToolbar() == false ) {
        continue;
      }

      if ( count > 0 && separatorPending ) {
        toolBar.addSeparator();
        separatorPending = false;
      }

      if ( actionPlugin instanceof ExportActionPlugin ) {
        final ExportActionPlugin exportPlugin = (ExportActionPlugin) actionPlugin;
        final ExportAction action = new ExportAction( exportPlugin, pane );
        toolBar.add( createButton( action, pane.getSwingGuiContext() ) );
        count += 1;
      } else if ( actionPlugin instanceof ControlActionPlugin ) {
        final ControlActionPlugin controlPlugin = (ControlActionPlugin) actionPlugin;
        final ControlAction action = new ControlAction( controlPlugin, pane );
        toolBar.add( createButton( action, pane.getSwingGuiContext() ) );
        count += 1;
      } else if ( actionPlugin instanceof ZoomListActionPlugin ) {
        final ZoomListActionPlugin zoomListActionPlugin = (ZoomListActionPlugin) actionPlugin;
        zoomListActionPlugin.setComponent( zoomSelector );

        final JPanel zoomPane = new JPanel();
        zoomPane.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        zoomPane.add( zoomSelector );
        toolBar.add( zoomPane );
        count += 1;
      }

      if ( actionPlugin.isSeparated() ) {
        separatorPending = true;
      }
    }
  }

  /**
   * Creates a button using the given action properties for the button's initialisation.
   *
   * @param action
   *          the action used to set up the button.
   * @return a button based on the supplied action.
   */
  private static JButton createButton( final Action action, final SwingGuiContext swingGuiContext ) {
    final JButton button = new JButton( action );
    boolean needText = true;
    if ( isLargeButtonsEnabled( swingGuiContext ) ) {
      final Icon icon = (Icon) action.getValue( SwingCommonModule.LARGE_ICON_PROPERTY );
      if ( icon != null && ( icon.getIconHeight() > 1 && icon.getIconHeight() > 1 ) ) {
        button.setIcon( icon );
        needText = false;
      }
    } else {
      final Icon icon = (Icon) action.getValue( Action.SMALL_ICON );
      if ( icon != null && ( icon.getIconHeight() > 1 && icon.getIconHeight() > 1 ) ) {
        button.setIcon( icon );
        needText = false;
      }
    }

    if ( needText ) {
      final Object value = action.getValue( Action.NAME );
      if ( value != null ) {
        button.setText( String.valueOf( value ) );
      }
    } else {
      button.setText( null );
    }

    final Object value = button.getAction().getValue( Action.ACCELERATOR_KEY );
    if ( value instanceof KeyStroke ) {
      button.unregisterKeyboardAction( (KeyStroke) value );
    }

    return button;
  }

  private static boolean isLargeButtonsEnabled( final SwingGuiContext swingGuiContext ) {
    final Configuration configuration = swingGuiContext.getConfiguration();
    if ( "true".equals( configuration.getConfigProperty( //$NON-NLS-1$
        "org.pentaho.reporting.engine.classic.core.modules.gui.base.LargeIcons" ) ) ) { //$NON-NLS-1$
      return true;
    }
    return false;
  }

  public static double getNextZoomOut( final double zoom, final double[] zoomFactors ) {
    if ( zoom <= zoomFactors[0] ) {
      return ( zoom * 2.0 ) / 3.0;
    }

    final double largestZoom = zoomFactors[zoomFactors.length - 1];
    if ( zoom > largestZoom ) {
      final double linear = ( zoom * 2.0 ) / 3.0;
      if ( linear < largestZoom ) {
        return largestZoom;
      }
      return linear;
    }

    for ( int i = zoomFactors.length - 1; i >= 0; i-- ) {
      final double factor = zoomFactors[i];
      if ( factor < zoom ) {
        return factor;
      }
    }

    return ( zoom * 2.0 ) / 3.0;
  }

  public static double getNextZoomIn( final double zoom, final double[] zoomFactors ) {
    final double largestZoom = zoomFactors[zoomFactors.length - 1];
    if ( zoom >= largestZoom ) {
      return ( zoom * 1.5 );
    }

    final double smallestZoom = zoomFactors[0];
    if ( zoom < smallestZoom ) {
      final double linear = ( zoom * 1.5 );
      if ( linear > smallestZoom ) {
        return smallestZoom;
      }
      return linear;
    }

    for ( int i = 0; i < zoomFactors.length; i++ ) {
      final double factor = zoomFactors[i];
      if ( factor > zoom ) {
        return factor;
      }
    }
    return ( zoom * 1.5 );
  }

  public static IconTheme createIconTheme( final Configuration config ) {
    final String themeClass = config.getConfigProperty( ICON_THEME_CONFIG_KEY );
    final Object maybeTheme = ObjectUtilities.loadAndInstantiate( themeClass, PreviewPane.class, IconTheme.class );
    final IconTheme iconTheme;
    if ( maybeTheme != null ) {
      iconTheme = (IconTheme) maybeTheme;
    } else {
      iconTheme = new DefaultIconTheme();
    }
    iconTheme.initialize( config );
    return iconTheme;
  }

  public static ActionFactory createActionFactory( final Configuration config ) {
    final String factoryClass = config.getConfigProperty( ACTION_FACTORY_CONFIG_KEY );
    final Object maybeFactory =
        ObjectUtilities.loadAndInstantiate( factoryClass, PreviewPane.class, ActionFactory.class );
    final ActionFactory actionFactory;
    if ( maybeFactory != null ) {
      actionFactory = (ActionFactory) maybeFactory;
    } else {
      actionFactory = new DefaultActionFactory();
    }
    return actionFactory;
  }

  public static CategoryTreeItem[] buildMenuTree( final ActionCategory[] categories ) {
    final CategoryTreeItem[] tree = new CategoryTreeItem[categories.length];
    for ( int i = 0; i < categories.length; i++ ) {
      final ActionCategory category = categories[i];
      tree[i] = new CategoryTreeItem( category );
    }

    for ( int j = 0; j < tree.length; j++ ) {
      final CategoryTreeItem item = tree[j];
      final String itemName = item.getName();
      int parentWeight = 0;
      CategoryTreeItem parent = null;
      // now for each item, find the best parent item.
      for ( int k = 0; k < tree.length; k++ ) {
        if ( k == j ) {
          // never add yourself ..
          continue;
        }
        final CategoryTreeItem treeItem = tree[k];
        final String parentName = treeItem.getName();
        if ( itemName.startsWith( parentName ) == false ) {
          continue;
        }
        if ( parentName.length() > parentWeight ) {
          parent = treeItem;
          parentWeight = parentName.length();
        }
      }

      item.setParent( parent );
    }

    for ( int j = 0; j < tree.length; j++ ) {
      final CategoryTreeItem item = tree[j];
      final CategoryTreeItem parent = item.getParent();
      if ( parent != null ) {
        parent.add( item );
      }
    }
    return tree;
  }

  public static Map<ActionCategory, ActionPlugin[]> loadActions( final SwingGuiContext swingGuiContext ) {
    final HashMap<ActionCategory, ActionPlugin[]> actions = new HashMap<ActionCategory, ActionPlugin[]>();

    final Configuration configuration = swingGuiContext.getConfiguration();
    final ActionCategory[] categories = loadCategories( swingGuiContext );
    final ActionFactory factory = PreviewPaneUtilities.createActionFactory( configuration );

    for ( int i = 0; i < categories.length; i++ ) {
      final ActionCategory category = categories[i];
      actions.put( category, factory.getActions( swingGuiContext, category.getName() ) );
    }
    return actions;
  }

  public static ActionCategory[] loadCategories( final SwingGuiContext swingGuiContext ) {
    final ArrayList categories = new ArrayList();
    final Configuration configuration = swingGuiContext.getConfiguration();
    final Iterator keys = configuration.findPropertyKeys( CATEGORY_PREFIX );
    while ( keys.hasNext() ) {
      final String enableKey = (String) keys.next();
      if ( enableKey.endsWith( ".enabled" ) == false ) { //$NON-NLS-1$
        continue;
      }

      if ( "true".equals( configuration.getConfigProperty( enableKey ) ) == false ) { //$NON-NLS-1$
        continue;
      }

      final String base = enableKey.substring( 0, enableKey.length() - ".enabled".length() ); //$NON-NLS-1$
      if ( base.length() == 0 ) {
        continue;
      }

      final String categoryKey = base.substring( CATEGORY_PREFIX.length() );
      final String className = configuration.getConfigProperty( base + ".class" ); //$NON-NLS-1$
      ActionCategory actionCategory;
      if ( className == null ) {
        actionCategory = new ActionCategory();
      } else {
        actionCategory =
            (ActionCategory) ObjectUtilities.loadAndInstantiate( className, PreviewPane.class, ActionCategory.class );
        if ( actionCategory == null ) {
          actionCategory = new ActionCategory();
        }
      }

      final String positionText = configuration.getConfigProperty( base + ".position" ); //$NON-NLS-1$
      actionCategory.setPosition( ParserUtil.parseInt( positionText, 0 ) );
      actionCategory.setName( categoryKey );
      actionCategory.setResourceBase( configuration.getConfigProperty( base + ".resource-base" ) ); //$NON-NLS-1$
      actionCategory.setResourcePrefix( configuration.getConfigProperty( base + ".resource-prefix" ) ); //$NON-NLS-1$
      actionCategory.initialize( swingGuiContext );
      categories.add( actionCategory );
    }

    return (ActionCategory[]) categories.toArray( new ActionCategory[categories.size()] );
  }
}
