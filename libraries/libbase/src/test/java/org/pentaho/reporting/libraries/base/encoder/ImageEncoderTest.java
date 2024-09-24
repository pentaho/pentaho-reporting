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

package org.pentaho.reporting.libraries.base.encoder;

import junit.framework.TestCase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageEncoderTest extends TestCase {
  public ImageEncoderTest() {
  }

  public ImageEncoderTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
  }

  public void testPngEncoderAvailable() throws UnsupportedEncoderException, IOException {
    assertTrue( ImageEncoderRegistry.getInstance().isEncoderAvailable( "image/png" ) );
    final ImageEncoder imageEncoder = ImageEncoderRegistry.getInstance().createEncoder( "image/png" );
    assertNotNull( imageEncoder );
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    imageEncoder.encodeImage( new BufferedImage( 10, 10, BufferedImage.TYPE_INT_ARGB ), bout, 0.75f, false );
    assertTrue( bout.toByteArray().length > 0 );
  }

  public void testJpegEncoderWorks() throws UnsupportedEncoderException, IOException {

    if ( ImageEncoderRegistry.getInstance().isEncoderAvailable( "image/jpg" ) == false ) {
      return;
    }

    final ImageEncoder imageEncoder = ImageEncoderRegistry.getInstance().createEncoder( "image/jpg" );
    assertNotNull( imageEncoder );
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    imageEncoder.encodeImage( new BufferedImage( 10, 10, BufferedImage.TYPE_INT_ARGB ), bout, 0.75f, false );
    assertTrue( bout.toByteArray().length > 0 );
  }
}
