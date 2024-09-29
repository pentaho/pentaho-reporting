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


package org.pentaho.reporting.engine.classic.core.event;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.states.ReportState;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportProgressEventTest {


  @Test
  public void testReuse() {
    final ReportState mock = mock( ReportState.class );
    when( mock.getCurrentRow() ).thenReturn( 1 );
    when( mock.getNumberOfRows() ).thenReturn( 1 );
    when( mock.getLevel() ).thenReturn( 1 );

    final ReportProgressEvent event = new ReportProgressEvent( "test" );
    //Do something but don't update total number of pages
    assertEquals( -1, event.getTotalPages() );
    event.reuse( ReportProgressEvent.PRECOMPUTING_VALUES, mock, 0 );
    assertEquals( -1, event.getTotalPages() );
    event.reuse( ReportProgressEvent.PAGINATING, mock, 100 );
    assertEquals( -1, event.getTotalPages() );
    //Update total number of pages
    event.reuse( ReportProgressEvent.GENERATING_CONTENT, mock, 100, 150 );
    assertEquals( 150, event.getTotalPages() );
    //Do something but don't update total number of pages
    event.reuse( ReportProgressEvent.PAGINATING, mock, 100 );
    //Expect that total number is not changed
    assertEquals( 150, event.getTotalPages() );
  }

}
