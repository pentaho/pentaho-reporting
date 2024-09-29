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

/**
 * Creation-Date: 29.04.2006, 14:57:44
 *
 * @author Thomas Morgner
 */
public interface EncodingCore {

  public boolean isUnicodeCharacterSupported( int c );

  /**
   * Encode, but ignore errors.
   *
   * @param text
   * @param buffer
   * @return
   */
  public ByteBuffer encode( CodePointBuffer text, ByteBuffer buffer )
    throws EncodingException;

  public CodePointBuffer decode( ByteBuffer text, CodePointBuffer buffer )
    throws EncodingException;

  public ByteBuffer encode( CodePointBuffer text, ByteBuffer buffer,
                            EncodingErrorType errorHandling )
    throws EncodingException;

  public CodePointBuffer decode( ByteBuffer text, CodePointBuffer buffer,
                                 EncodingErrorType errorHandling )
    throws EncodingException;

}
