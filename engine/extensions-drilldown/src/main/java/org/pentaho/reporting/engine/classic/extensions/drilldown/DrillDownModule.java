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

package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.net.URL;

public class DrillDownModule extends AbstractModule {
  public static final String DRILLDOWN_PROFILE_NAMESPACE =
    "http://reporting.pentaho.org/namespaces/engine/classic/drilldown-profile/1.0";

  public DrillDownModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws org.pentaho.reporting.libraries.base.boot.ModuleInitializeException if an error ocurred while initializing
   *                                                                             the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    final URL expressionMetaSource = ObjectUtilities.getResource
      ( "org/pentaho/reporting/engine/classic/extensions/drilldown/drilldown-profile.xml", DrillDownModule.class );
    if ( expressionMetaSource == null ) {
      throw new ModuleInitializeException( "Error: Could not find the drilldown meta-data description file" );
    }
    register( expressionMetaSource );

    final URL customizedMetaSource = ObjectUtilities.getResource( "drilldown-profile.xml", DrillDownModule.class );
    if ( customizedMetaSource != null ) {
      register( customizedMetaSource );
    }
  }

  private void register( final URL expressionMetaSource )
    throws ModuleInitializeException {
    if ( expressionMetaSource == null ) {
      return;
    }
    try {
      DrillDownProfileMetaData.getInstance().registerFromXml( expressionMetaSource );
    } catch ( Exception e ) {
      throw new ModuleInitializeException( "Error: Could not parse the drilldown meta-data description file", e );
    }
  }
}
