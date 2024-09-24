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

package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import org.pentaho.reporting.designer.core.editor.drilldown.basic.XulDrillDownUiProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfileMetaData;

public class PentahoSugarDrillDownUiProfile extends XulDrillDownUiProfile {
  public PentahoSugarDrillDownUiProfile() {
    final DrillDownProfile[] profiles =
      DrillDownProfileMetaData.getInstance().getDrillDownProfileByGroup( "pentaho-sugar" );
    final String[] profileNames = new String[ profiles.length ];
    for ( int i = 0; i < profileNames.length; i++ ) {
      profileNames[ i ] = profiles[ i ].getName();
    }

    init( profileNames );
  }

  public int getOrderKey() {
    return 3000;
  }
}
