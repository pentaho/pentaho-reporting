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
