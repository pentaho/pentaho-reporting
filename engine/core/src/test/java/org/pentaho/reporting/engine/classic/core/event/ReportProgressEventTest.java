/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
