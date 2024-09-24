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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper.EncodingUtilities;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class EncodingUtilitiesTest extends TestCase {
  public EncodingUtilitiesTest( final String s ) {
    super( s );
  }

  public void testCreateUTF16() throws UnsupportedEncodingException {
    final EncodingUtilities ut = new EncodingUtilities( "UTF-16" );
    assertEquals( "UTF-16", ut.getEncoding() );
    assertEquals( 2, ut.getSpace().length );
    assertEquals( 2, ut.getHeader().length );
    assertTrue( "HeaderContents", Arrays.equals( new byte[] { -2, -1 }, ut.getHeader() ) );
    assertTrue( "SpaceContents", Arrays.equals( new byte[] { 0, 32 }, ut.getSpace() ) );
  }

  public void testCreateUTF8() throws UnsupportedEncodingException {
    final EncodingUtilities ut = new EncodingUtilities( "UTF-8" );
    assertEquals( "UTF-8", ut.getEncoding() );
    assertEquals( 1, ut.getSpace().length );
    assertEquals( 0, ut.getHeader().length );
    assertTrue( "SpaceContents", Arrays.equals( new byte[] { 32 }, ut.getSpace() ) );
  }

  public void testCreateASCII() throws UnsupportedEncodingException {
    final EncodingUtilities ut = new EncodingUtilities( "ASCII" );
    assertEquals( "ASCII", ut.getEncoding() );
    assertEquals( 1, ut.getSpace().length );
    assertEquals( 0, ut.getHeader().length );
    assertTrue( "SpaceContents", Arrays.equals( new byte[] { 32 }, ut.getSpace() ) );
  }

}
