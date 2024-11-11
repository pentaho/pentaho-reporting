/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
