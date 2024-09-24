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

package org.pentaho.reporting.engine.classic.extensions.toc;

import junit.framework.TestCase;

public class IndexUtilityTest extends TestCase {
  public IndexUtilityTest() {
  }

  public IndexUtilityTest( final String name ) {
    super( name );
  }

  public void testCondensedText() {
    assertEquals( "", IndexUtility.getCondensedIndexText( new Integer[ 0 ], "," ) );
    assertEquals( "1-3,3-4", IndexUtility.getCondensedIndexText( new Integer[] { 1, 2, 3, 3, 4 }, "," ) );
    assertEquals( "1-4", IndexUtility.getCondensedIndexText( new Integer[] { 1, 2, 3, 4 }, "," ) );
    assertEquals( "1,2,4-6,8", IndexUtility.getCondensedIndexText( new Integer[] { 1, 2, 4, 5, 6, 8 }, "," ) );
    assertEquals( "1,2,4-8", IndexUtility.getCondensedIndexText( new Integer[] { 1, 2, 4, 5, 6, 7, 8 }, "," ) );

  }
}
