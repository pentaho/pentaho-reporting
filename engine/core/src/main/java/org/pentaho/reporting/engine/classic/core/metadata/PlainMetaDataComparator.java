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
