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

package org.pentaho.reporting.designer.core.editor.drilldown;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010 Time: 13:25:17
 *
 * @author Thomas Morgner.
 */
public class DrillDownUiProfileRegistry {
  private static DrillDownUiProfileRegistry instance;
  private HashMap<String, DrillDownUiProfile> registry;
  private static final String PREFIX = "org.pentaho.reporting.designer.core.editor.drilldown.profiles.";

  public static synchronized DrillDownUiProfileRegistry getInstance() {
    if ( instance == null ) {
      instance = new DrillDownUiProfileRegistry();
      instance.initialize();
    }
    return instance;
  }

  private DrillDownUiProfileRegistry() {
    this.registry = new HashMap<String, DrillDownUiProfile>();
  }

  public void addProfile( final String name, final DrillDownUiProfile profile ) {
    if ( profile == null ) {
      throw new NullPointerException();
    }
    registry.put( name, profile );
  }

  public void initialize() {
    final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
    final Iterator keys = configuration.findPropertyKeys( PREFIX );//NON-NLS
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      final String name = key.substring( PREFIX.length() );
      final String className = configuration.getConfigProperty( key );
      if ( className == null ) {
        DebugLog.log( "No such profile: " + key );//NON-NLS
        continue;
      }
      final DrillDownUiProfile profile = (DrillDownUiProfile) ObjectUtilities.loadAndInstantiate
        ( className, DrillDownEditor.class, DrillDownUiProfile.class );
      if ( profile == null ) {
        DebugLog.log( "Invalid profile: " + key );//NON-NLS
        continue;
      }
      addProfile( name, profile );
    }
  }

  public DrillDownUiProfile getProfile( String name ) {
    return registry.get( name );
  }

  public DrillDownUiProfile[] getProfiles() {
    return registry.values().toArray( new DrillDownUiProfile[ registry.size() ] );
  }

  public int getProfileCount() {
    return registry.size();
  }
}
