/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.base.encoder;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A image encoder. The encoder's encodeImage method must be synchronized in some way, so that multiple calls from
 * multiple threads do not interact with each other.
 *
 * @author Thomas Morgner
 */
public interface ImageEncoder {
  /**
   * Encodes the given image using the given encoder-specific quality and alpha-channel settings and writes the encoded
   * image-data to the given stream.
   *
   * @param image        the image to be encoded.
   * @param outputStream the output stream, where to write the image data to.
   * @param quality      the quality of the encoding.
   * @param encodeAlpha  a flag controlling whether the alpha-channel should be encoded as well.
   * @throws IOException                 if there was an IO error while generating or writing the image data.
   * @throws UnsupportedEncoderException if the encoder is not supported.
   */
  public void encodeImage( final Image image,
                           final OutputStream outputStream,
                           final float quality,
                           final boolean encodeAlpha ) throws IOException, UnsupportedEncoderException;

  /**
   * Returns the mime-type of the encoded data.
   *
   * @return the mime-type.
   */
  public String getMimeType();
}
