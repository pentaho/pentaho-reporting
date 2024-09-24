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
