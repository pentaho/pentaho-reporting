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


package org.pentaho.reporting.designer.core.editor.drilldown;

import java.util.Comparator;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010 Time: 14:18:08
 *
 * @author Thomas Morgner.
 */
public class DrillDownUiProfileComparator implements Comparator {
  public DrillDownUiProfileComparator() {
  }

  public int compare( final Object o1, final Object o2 ) {
    if ( o1 == null && o2 == null ) {
      return 0;
    }
    if ( o1 == null ) {
      return -1;
    }
    if ( o2 == null ) {
      return +1;
    }

    final DrillDownUiProfile p1 = (DrillDownUiProfile) o1;
    final DrillDownUiProfile p2 = (DrillDownUiProfile) o2;

    if ( p1.getOrderKey() < p2.getOrderKey() ) {
      return -1;
    }
    if ( p1.getOrderKey() > p2.getOrderKey() ) {
      return +1;
    }
    return p1.getDisplayName().compareTo( p2.getDisplayName() );
  }
}
