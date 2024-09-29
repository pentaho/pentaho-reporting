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


package org.pentaho.reporting.designer.core;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ReportDesignerUiPluginRegistry {
  private static final String PREFIX = "org.pentaho.reporting.designer.modules.ui-extensions.";

  private static ReportDesignerUiPluginRegistry instance;
  private ArrayList<ReportDesignerUiPlugin> factories;

  public static synchronized ReportDesignerUiPluginRegistry getInstance() {
    if ( instance == null ) {
      instance = new ReportDesignerUiPluginRegistry();
      instance.register();
    }
    return instance;
  }

  public ReportDesignerUiPluginRegistry() {
    factories = new ArrayList();
  }

  private void register() {
    final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
    final Iterator keys = configuration.findPropertyKeys( PREFIX );
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      if ( key.endsWith( ".UiPlugin" ) )// NON-NLS
      {
        final String className = configuration.getConfigProperty( key );
        final ReportDesignerUiPlugin plugin = (ReportDesignerUiPlugin)
          ObjectUtilities.loadAndInstantiate( className, ReportDesignerUiPluginRegistry.class,
            ReportDesignerUiPlugin.class );
        if ( plugin != null ) {
          factories.add( plugin );
        }
      }
    }
  }

  public void addPlugin( final ReportDesignerUiPlugin plugin ) {
    if ( plugin == null ) {
      throw new NullPointerException();
    }
    factories.add( plugin );
  }

  public ReportDesignerUiPlugin[] getPlugins() {
    return factories.toArray( new ReportDesignerUiPlugin[ factories.size() ] );
  }
}
