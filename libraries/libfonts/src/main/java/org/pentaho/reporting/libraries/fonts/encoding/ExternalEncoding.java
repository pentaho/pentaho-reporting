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

import org.pentaho.reporting.libraries.resourceloader.Resource;

import java.util.Locale;

/**
 * Creation-Date: 29.04.2006, 14:49:06
 *
 * @author Thomas Morgner
 */
public final class ExternalEncoding implements Encoding {

  private String name;
  private EncodingCore core;

  /**
   * We keep a stong reference to our source, so that this thing won't be recycled as long as one instance is in use.
   */
  private Resource resource;

  public ExternalEncoding( final String name,
                           final EncodingCore core,
                           final Resource resource ) {
    this.name = name;
    this.core = core;
    this.resource = resource;
  }

  public String getName() {
    return name;
  }

  public String getName( final Locale locale ) {
    return name;
  }

  public boolean isUnicodeCharacterSupported( final int c ) {
    return core.isUnicodeCharacterSupported( c );
  }

  public Resource getResource() {
    return resource;
  }

  public ByteBuffer encode( final CodePointBuffer text, final ByteBuffer buffer )
    throws EncodingException {
    return core.encode( text, buffer );
  }

  public ByteBuffer encode( final CodePointBuffer text, final ByteBuffer buffer,
                            final EncodingErrorType errorHandling )
    throws EncodingException {
    return core.encode( text, buffer, errorHandling );
  }

  public CodePointBuffer decode( final ByteBuffer text,
                                 final CodePointBuffer buffer ) throws
    EncodingException {
    return core.decode( text, buffer );
  }

  public CodePointBuffer decode( final ByteBuffer text,
                                 final CodePointBuffer buffer,
                                 final EncodingErrorType errorHandling )
    throws EncodingException {
    return core.decode( text, buffer, errorHandling );
  }
}
