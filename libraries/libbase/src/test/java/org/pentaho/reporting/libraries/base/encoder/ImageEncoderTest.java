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
