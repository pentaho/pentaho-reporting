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


package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * Creation-Date: 16.11.2006, 16:28:03
 *
 * @author Thomas Morgner
 */
public class DefaultActionFactory implements ActionFactory {
  private static final Log logger = LogFactory.getLog( DefaultActionFactory.class );
  private static final ActionPlugin[] EMPTY_ACTIONS = new ActionPlugin[0];
  private static final String PREFIX = "org.pentaho.reporting.engine.classic.core.modules.gui.swing.actions."; //$NON-NLS-1$

  private static final Messages MESSAGES = new Messages( Locale.getDefault(), SwingCommonModule.BUNDLE_NAME,
      ObjectUtilities.getClassLoader( SwingCommonModule.class ) );

  public DefaultActionFactory() {
  }

  public ActionPlugin[] getActions( final SwingGuiContext context, final String category ) {
    if ( context == null ) {
      throw new NullPointerException();
    }
    if ( category == null ) {
      throw new NullPointerException();
    }

    final Configuration configuration = context.getConfiguration();
    final String prefix = DefaultActionFactory.PREFIX + category;
    final Iterator keys = configuration.findPropertyKeys( prefix );
    if ( keys.hasNext() == false ) {
      DefaultActionFactory.logger.debug( DefaultActionFactory.MESSAGES.getString(
          "DefaultActionFactory.DEBUG_NO_ACTIONS", category ) ); //$NON-NLS-1$
      return DefaultActionFactory.EMPTY_ACTIONS;
    }

    final HashMap plugins = new HashMap();
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      final String base = key.substring( prefix.length() );
      if ( isPluginKey( base ) == false ) {
        // Maybe an invalid key or a key for a sub-category ..
        continue;
      }

      final String clazz = configuration.getConfigProperty( key );
      final Object maybeActionPlugin =
          ObjectUtilities.loadAndInstantiate( clazz, DefaultActionFactory.class, ActionPlugin.class );
      if ( maybeActionPlugin == null ) {
        DefaultActionFactory.logger.debug( DefaultActionFactory.MESSAGES.getString(
            "DefaultActionFactory.DEBUG_NOT_ACTION_PLUGIN", category, clazz ) ); //$NON-NLS-1$
        continue;
      }

      final ActionPlugin plugin = (ActionPlugin) maybeActionPlugin;
      if ( plugin.initialize( context ) == false ) {
        plugin.deinitialize( context );
        continue;
      }

      final String role = plugin.getRole();
      if ( role == null ) {
        plugins.put( plugin, plugin );
      } else {
        final ActionPlugin otherPlugin = (ActionPlugin) plugins.get( role );
        if ( otherPlugin != null ) {
          if ( plugin.getRolePreference() > otherPlugin.getRolePreference() ) {
            plugins.put( role, plugin );
            otherPlugin.deinitialize( context );
          } else {
            DefaultActionFactory.logger.debug( DefaultActionFactory.MESSAGES.getString(
                "DefaultActionFactory.DEBUG_PLUGIN_OVERRIDE", category, clazz, otherPlugin.getClass().getName() ) ); //$NON-NLS-1$
          }
        } else {
          plugins.put( role, plugin );
        }
      }
    }

    DefaultActionFactory.logger.debug( DefaultActionFactory.MESSAGES.getString(
        "DefaultActionFactory.DEBUG_RETURNING_PLUGINS", String.valueOf( plugins.size() ), category ) ); //$NON-NLS-1$ //$NON-NLS-2$

    return (ActionPlugin[]) plugins.values().toArray( new ActionPlugin[plugins.size()] );
  }

  private boolean isPluginKey( final String base ) {
    if ( base.length() < 1 ) {
      return false;
    }
    if ( base.indexOf( '.', 1 ) > 0 ) {
      return false;
    }
    return true;
  }
}
