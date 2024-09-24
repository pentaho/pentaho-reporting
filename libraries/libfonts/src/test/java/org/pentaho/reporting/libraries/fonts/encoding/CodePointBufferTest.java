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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.fonts.encoding;

import junit.framework.TestCase;

/**
 * Creation-Date: 23.04.2006, 17:28:07
 *
 * @author Thomas Morgner
 */
public class CodePointBufferTest extends TestCase {
  public CodePointBufferTest() {
  }

  public CodePointBufferTest( final String string ) {
    super( string );
  }

  public void testWrite() {
    final CodePointBuffer buffer = new CodePointBuffer( 0 );
    final CodePointStream cps = new CodePointStream( buffer, 10 );
    cps.put( 10 );
    cps.put( 11 );
    cps.put( 12 );
    cps.put( 13 );

    cps.put( new int[] { 20, 21, 22, 23, 24, 25 } );
    cps.close();

    assertEquals( "Buffer-Cursor: ", 10, buffer.getCursor() );
  }
}
