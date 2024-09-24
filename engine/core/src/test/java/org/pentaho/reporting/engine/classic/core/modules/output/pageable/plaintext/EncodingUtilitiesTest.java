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
