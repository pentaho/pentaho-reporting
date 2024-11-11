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


package org.pentaho.reporting.engine.classic.core.designtime.compat;

import java.util.Comparator;

public class CompatibilityConverterComparator implements Comparator<CompatibilityConverter> {
  public CompatibilityConverterComparator() {
  }

  public int compare( final CompatibilityConverter o1, final CompatibilityConverter o2 ) {
    if ( o1 == o2 ) {
      return 0;
    }
    if ( o1 == null ) {
      return -1;
    }
    if ( o2 == null ) {
      return -1;
    }
    final int v1 = o1.getTargetVersion();
    final int v2 = o2.getTargetVersion();
    if ( v1 == v2 ) {
      return 0;
    }
    if ( v1 < v2 ) {
      return -1;
    }
    return +1;
  }
}
