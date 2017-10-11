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

/**
 * This Comparator implements the standard java.util.Comparator interface. The intended use is to group MetaData
 * elements based on either: (1) The ordinal value in the groupsorting.properties file (see documentation in that file
 * for how this is accomplished. Any group(s) not represented in that file will be displayed last if more than one group
 * they will all be displayed last in alpha order. (2) If the groupsorting.properties doesn't exist or can't be loaded,
 * the value of the *.grouping key (using the standard string compare).
 * <p/>
 * This effectively sorts the attribute and style elements into groups for display by a GUI. Fine grain ordering of the
 * groups can be accomplished using the groupsorting.properties file.
 * <p/>
 * Documented by William Seyler
 *
 * @author Thomas Morgner
 */
public class GroupedMetaDataComparator implements Comparator<MetaData> {
  public static final GroupedMetaDataComparator DEFAULT = new GroupedMetaDataComparator();
  public static final GroupedMetaDataComparator ENGLISH = new GroupedMetaDataComparator( Locale.ENGLISH );

  /**
   *
   */
  private Locale locale;

  public GroupedMetaDataComparator() {
    locale = Locale.getDefault();
  }

  public GroupedMetaDataComparator( final Locale locale ) {
    this.locale = locale;
  }

  public int compare( final MetaData metaData1, final MetaData metaData2 ) {
    // Look to the ordinal to determine the position
    final int groupOrd1 = metaData1.getGroupingOrdinal( locale );
    final int groupOrd2 = metaData2.getGroupingOrdinal( locale );

    if ( groupOrd1 != groupOrd2 ) {
      return groupOrd1 < groupOrd2 ? -1 : 1;
    }

    // Picks up the difference in group name if the above was the same
    final String gr1 = metaData1.getGrouping( locale );
    final String gr2 = metaData2.getGrouping( locale );
    final int result = gr1.compareTo( gr2 );
    if ( result != 0 ) {
      return result;
    }

    // At this point the groupings are the same so we have to look at the
    // Item ordinals to determine where in the group they go
    final int itemOrd1 = metaData1.getItemOrdinal( locale );
    final int itemOrd2 = metaData2.getItemOrdinal( locale );
    if ( itemOrd1 != itemOrd2 ) {
      return itemOrd1 < itemOrd2 ? -1 : 1;
    }

    // if we've gotten this far then we down to doing it alphabetically on
    // the items display name
    final String dn1 = metaData1.getMetaAttribute( "display-name", locale );
    final String dn2 = metaData2.getMetaAttribute( "display-name", locale );
    if ( dn1 == null && dn2 == null ) {
      return 0;
    }
    if ( dn1 == null ) {
      return -1;
    }
    if ( dn2 == null ) {
      return +1;
    }
    return dn1.compareTo( dn2 );
  }

}
