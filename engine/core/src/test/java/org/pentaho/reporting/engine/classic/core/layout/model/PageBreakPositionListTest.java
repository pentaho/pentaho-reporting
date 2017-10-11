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

package org.pentaho.reporting.engine.classic.core.layout.model;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

public class PageBreakPositionListTest extends TestCase {
  private PageBreakPositionList tester;

  public PageBreakPositionListTest() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();

    tester = new PageBreakPositionList();
    tester.addMajorBreak( 0, 0 );
    tester.addMinorBreak( 5000 );
    tester.addMajorBreak( 10000, 0 );
    tester.addMinorBreak( 15000 );
    tester.addMajorBreak( 20000, 0 );
    tester.addMinorBreak( 25000 );
    tester.addMajorBreak( 30000, 0 );
  }

  public void testFindNextBreakPosition() {
    assertEquals( "Sitting before the pagebreak: ", 0, tester.findNextBreakPosition( -1 ) );
    assertEquals( "Sitting at the pagebreak: ", 0, tester.findNextBreakPosition( 0 ) );
    assertEquals( "Sitting behind the pagebreak: ", 5000, tester.findNextBreakPosition( 1 ) );
    assertEquals( "Sitting on the last pagebreak: ", 30000, tester.findNextBreakPosition( 30000 ) );
    assertEquals( "Sitting behind the last pagebreak: ", 30000, tester.findNextBreakPosition( 40000 ) );
  }

  public void testIsCrossingPagebreak() {

    assertFalse( "Y=-5000; Height=5000", tester.isCrossingPagebreak( -5000, 5000, 0 ) );
    assertFalse( "Y=10; Height=500", tester.isCrossingPagebreak( 10, 500, 0 ) );
    // A box with height of zero does not cross the pagebreak.
    assertFalse( "Y=0; Height=0", tester.isCrossingPagebreak( 0, 0, 0 ) );
    // A box with the height equal to the page height will not cross the pagebreak
    assertFalse( "Y=0; Height=5000", tester.isCrossingPagebreak( 0, 5000, 0 ) );
    // This one will .
    assertTrue( "Y=2500; Height=5000", tester.isCrossingPagebreak( 2500, 5000, 0 ) );
    // A box with height of zero does not cross the pagebreak.
    assertFalse( "Y=0; Height=0; shift=5000", tester.isCrossingPagebreak( 0, 0, 5000 ) );
    // A box with the height equal to the page height will not cross the pagebreak
    assertFalse( "Y=0; Height=5000; shift=5000", tester.isCrossingPagebreak( 0, 5000, 5000 ) );
    // This one will .
    assertTrue( "Y=2500; Height=5000; Shift=5000", tester.isCrossingPagebreak( 2500, 5000, 5000 ) );
    // A box that sits after the last pagebreak will not cross a pagebreak.
    assertFalse( "Y=30500; Height=5000; Shift=5000", tester.isCrossingPagebreak( 30500, 5000, 5000 ) );
  }

  public void testFindNextMajorBreakPosition() {
    assertEquals( "Sitting before the pagebreak: ", 0, tester.findNextMajorBreakPosition( -1 ) );
    assertEquals( "Sitting at the pagebreak: ", 0, tester.findNextMajorBreakPosition( 0 ) );
    assertEquals( "Sitting behind the pagebreak: ", 10000, tester.findNextMajorBreakPosition( 1 ) );
    assertEquals( "Sitting on the last pagebreak: ", 30000, tester.findNextMajorBreakPosition( 30000 ) );
    assertEquals( "Sitting behind the last pagebreak: ", 30000, tester.findNextMajorBreakPosition( 40000 ) );
  }

  public void testFindPageEndForPageStartPositionSmall() {
    PageBreakPositionList list = new PageBreakPositionList();
    list.addMajorBreak( 0, 0 );
    list.addMajorBreak( 100000, 0 );

    assertEquals( 100000, list.findPageEndForPageStartPosition( 0 ) );
    assertEquals( 100000, list.findPageEndForPageStartPosition( 100000 ) );
  }

  public void testFindPageEndForPageStartPositionLarge() {
    PageBreakPositionList list = new PageBreakPositionList();
    list.addMajorBreak( 0, 0 );
    list.addMajorBreak( 100000, 0 );
    list.addMajorBreak( 200000, 0 );
    list.addMajorBreak( 300000, 0 );

    assertEquals( 100000, list.findPageEndForPageStartPosition( 0 ) );
    assertEquals( 300000, list.findPageEndForPageStartPosition( 200000 ) );
    assertEquals( 300000, list.findPageEndForPageStartPosition( 300000 ) );
  }

  public void testWeirdCasePrd5034() {
    PageBreakPositionList list = new PageBreakPositionList();
    list.addMajorBreak( 58446100, 14300000 );
    assertEquals( 58446100, list.findNextBreakPosition( 9271300 ) );
    assertFalse( list.isCrossingPagebreak( 9271300, 1000600, 0 ) );
    assertFalse( list.isCrossingPagebreak( 1009271300, 1000600, 0 ) );
    assertFalse( list.isCrossingPagebreak( 9271300, 1000600, 0 ) );
  }

}
