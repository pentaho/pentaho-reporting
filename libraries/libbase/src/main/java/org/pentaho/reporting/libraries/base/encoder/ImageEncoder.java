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
