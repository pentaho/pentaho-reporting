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
