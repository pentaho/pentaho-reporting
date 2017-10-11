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

public class PlainMetaDataComparatorTest {

  private Locale locale = Locale.getDefault();
  private PlainMetaDataComparator comparator = new PlainMetaDataComparator();

  @Test
  public void testCompare() {
    MetaData metaData1 = mock( MetaData.class );
    MetaData metaData2 = mock( MetaData.class );

    doReturn( "name_0" ).when( metaData1 ).getDisplayName( locale );
    doReturn( "name_1" ).when( metaData2 ).getDisplayName( locale );
    assertThat( comparator.compare( metaData1, metaData2 ), is( equalTo( -1 ) ) );

    doReturn( "name_0" ).when( metaData2 ).getDisplayName( locale );
    doReturn( "1" ).when( metaData1 ).getGrouping( locale );
    doReturn( "0" ).when( metaData2 ).getGrouping( locale );
    assertThat( comparator.compare( metaData1, metaData2 ), is( equalTo( 1 ) ) );

  }
}
