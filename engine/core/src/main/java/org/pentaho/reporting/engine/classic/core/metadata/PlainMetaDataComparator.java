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


package org.pentaho.reporting.engine.classic.core.metadata;

import java.util.Comparator;
import java.util.Locale;

public class PlainMetaDataComparator implements Comparator<MetaData> {
  private Locale locale;

  public PlainMetaDataComparator() {
    locale = Locale.getDefault();
  }

  public int compare( final MetaData metaData1, final MetaData metaData2 ) {
    final String s1 = metaData1.getDisplayName( locale );
    final String s2 = metaData2.getDisplayName( locale );
    final int nameCompareResult = s1.compareTo( s2 );
    if ( nameCompareResult != 0 ) {
      return nameCompareResult;
    }

    final String g1 = metaData1.getGrouping( locale );
    final String g2 = metaData2.getGrouping( locale );
    return g1.compareTo( g2 );
  }
}
