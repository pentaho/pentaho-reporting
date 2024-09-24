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
