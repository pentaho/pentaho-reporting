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
