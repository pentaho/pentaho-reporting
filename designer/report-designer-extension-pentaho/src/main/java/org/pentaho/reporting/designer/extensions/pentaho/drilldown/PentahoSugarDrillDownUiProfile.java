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
