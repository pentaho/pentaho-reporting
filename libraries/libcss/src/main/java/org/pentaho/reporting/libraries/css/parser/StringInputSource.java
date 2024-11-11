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


package org.pentaho.reporting.libraries.css.parser;

import org.w3c.css.sac.InputSource;

import java.io.StringReader;
import java.net.URL;

/**
 * Creation-Date: 08.12.2005, 21:16:26
 *
 * @author Thomas Morgner
 */
public class StringInputSource extends InputSource {
  private StringReader reader;
  private URL baseUrl;

  /**
   * Zero-argument default constructor.
   *
   * @see #setURI
   * @see #setByteStream
   * @see #setCharacterStream
   * @see #setEncoding
   */
  public StringInputSource( final String data, final URL baseUrl ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    if ( baseUrl == null ) {
      throw new NullPointerException();
    }
    this.reader = new StringReader( data );
    this.baseUrl = baseUrl;
    setCharacterStream( reader );
    setURI( baseUrl.toExternalForm() );
  }

  public StringReader getReader() {
    return reader;
  }

  public URL getBaseUrl() {
    return baseUrl;
  }
}
