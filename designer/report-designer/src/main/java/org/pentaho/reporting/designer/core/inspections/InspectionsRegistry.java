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

package org.pentaho.reporting.designer.core.inspections;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.ArrayList;
import java.util.Iterator;

public class InspectionsRegistry {
  static final String PREFIX = "org.pentaho.reporting.designer.inspections.";

  private static InspectionsRegistry instance;
  private ArrayList<Inspection> factories;

  public static synchronized InspectionsRegistry getInstance() {
    if ( instance == null ) {
      instance = new InspectionsRegistry();
      instance.register();
    }
    return instance;
  }

  public InspectionsRegistry() {
    factories = new ArrayList<Inspection>();
  }

  private void register() {
    final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
    final Iterator keys = configuration.findPropertyKeys( PREFIX );
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      final String className = configuration.getConfigProperty( key );
      final Inspection plugin =
        ObjectUtilities.loadAndInstantiate( className, InspectionsRegistry.class, Inspection.class );
      if ( plugin != null ) {
        factories.add( plugin );
      }
    }
  }

  public void addInspection( final Inspection plugin ) {
    if ( plugin == null ) {
      throw new NullPointerException();
    }
    factories.add( plugin );
  }

  public Inspection[] getInspections() {
    return factories.toArray( new Inspection[ factories.size() ] );
  }
}
