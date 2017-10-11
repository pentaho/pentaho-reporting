/*
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Locale;

import org.junit.Test;

public class GroupedMetaDataComparatorTest {

  private Locale locale = Locale.getDefault();
  private GroupedMetaDataComparator comparator = new GroupedMetaDataComparator();

  @Test
  public void testCompare() {
    MetaData metaData1 = mock( MetaData.class );
    MetaData metaData2 = mock( MetaData.class );

    doReturn( 0 ).when( metaData1 ).getGroupingOrdinal( locale );
    doReturn( 1 ).when( metaData2 ).getGroupingOrdinal( locale );
    assertThat( comparator.compare( metaData1, metaData2 ), is( equalTo( -1 ) ) );

    doReturn( 1 ).when( metaData1 ).getGroupingOrdinal( locale );
    doReturn( 0 ).when( metaData2 ).getGroupingOrdinal( locale );
    assertThat( comparator.compare( metaData1, metaData2 ), is( equalTo( 1 ) ) );

    doReturn( 1 ).when( metaData2 ).getGroupingOrdinal( locale );
    doReturn( "1" ).when( metaData1 ).getGrouping( locale );
    doReturn( "0" ).when( metaData2 ).getGrouping( locale );
    assertThat( comparator.compare( metaData1, metaData2 ), is( equalTo( 1 ) ) );

    doReturn( "1" ).when( metaData2 ).getGrouping( locale );
    doReturn( 0 ).when( metaData1 ).getItemOrdinal( locale );
    doReturn( 1 ).when( metaData2 ).getItemOrdinal( locale );
    assertThat( comparator.compare( metaData1, metaData2 ), is( equalTo( -1 ) ) );

    doReturn( 1 ).when( metaData1 ).getItemOrdinal( locale );
    doReturn( 0 ).when( metaData2 ).getItemOrdinal( locale );
    assertThat( comparator.compare( metaData1, metaData2 ), is( equalTo( 1 ) ) );

    doReturn( 1 ).when( metaData2 ).getItemOrdinal( locale );
    doReturn( null ).when( metaData1 ).getMetaAttribute( "display-name", locale );
    doReturn( null ).when( metaData2 ).getMetaAttribute( "display-name", locale );
    assertThat( comparator.compare( metaData1, metaData2 ), is( equalTo( 0 ) ) );

    doReturn( null ).when( metaData1 ).getMetaAttribute( "display-name", locale );
    doReturn( "none" ).when( metaData2 ).getMetaAttribute( "display-name", locale );
    assertThat( comparator.compare( metaData1, metaData2 ), is( equalTo( -1 ) ) );

    doReturn( "none" ).when( metaData1 ).getMetaAttribute( "display-name", locale );
    doReturn( null ).when( metaData2 ).getMetaAttribute( "display-name", locale );
    assertThat( comparator.compare( metaData1, metaData2 ), is( equalTo( 1 ) ) );

    doReturn( "none" ).when( metaData2 ).getMetaAttribute( "display-name", locale );
    assertThat( comparator.compare( metaData1, metaData2 ), is( equalTo( 0 ) ) );
  }
}
