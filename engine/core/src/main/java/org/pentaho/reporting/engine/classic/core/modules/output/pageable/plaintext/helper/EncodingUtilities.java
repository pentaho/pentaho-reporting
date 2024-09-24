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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class EncodingUtilities {
  /**
   * The encoding name.
   */
  private String encoding;
  /**
   * The header sequence of the encoding (-2, -1 for UTF-16).
   */
  private byte[] header;
  /**
   * A single encoded space character.
   */
  private byte[] space;
  /**
   * The header plus the single space.
   */
  private byte[] encodingHeader;

  public EncodingUtilities( final String codepage ) throws UnsupportedEncodingException {
    if ( codepage == null ) {
      throw new NullPointerException();
    }

    this.encoding = codepage;

    encodingHeader = " ".getBytes( codepage );
    final byte[] spacesWithHeader = "  ".getBytes( codepage );
    final int spaceCharLength = spacesWithHeader.length - encodingHeader.length;
    space = new byte[spaceCharLength];
    header = new byte[encodingHeader.length - spaceCharLength];

    System.arraycopy( spacesWithHeader, encodingHeader.length, space, 0, spaceCharLength );
    System.arraycopy( encodingHeader, 0, header, 0, header.length );
  }

  public byte[] getSpace() {
    return space;
  }

  public byte[] getHeader() {
    return header;
  }

  /**
   * Writes encoded text for the current encoding into the output stream.
   *
   * @param textString
   *          the text that should be written.
   * @throws java.io.IOException
   *           if an error occures.
   */
  public void writeEncodedText( final char[] textString, final OutputStream out ) throws IOException {
    if ( textString == null ) {
      throw new NullPointerException();
    }

    final StringBuffer buffer = new StringBuffer( " " );
    buffer.append( textString );
    final byte[] text = buffer.toString().getBytes( encoding );
    out.write( text, encodingHeader.length, text.length - encodingHeader.length );
  }

  /**
   * Writes encoded text for the current encoding into the output stream.
   *
   * @param textString
   *          the text that should be written.
   * @throws java.io.IOException
   *           if an error occures.
   */
  public void writeEncodedText( final String textString, final OutputStream out ) throws IOException {
    if ( textString == null ) {
      throw new NullPointerException();
    }

    final StringBuffer buffer = new StringBuffer( " " );
    buffer.append( textString );
    final byte[] text = buffer.toString().getBytes( encoding );
    out.write( text, encodingHeader.length, text.length - encodingHeader.length );
  }

  public String getEncoding() {
    return encoding;
  }

  public byte[] getEncodingHeader() {
    return header;
  }
}
