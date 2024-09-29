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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * Creation-Date: 23.11.2005, 18:22:34
 *
 * @author Thomas Morgner
 */
public class URLInputSource extends InputSource {
  private URL url;

  public URLInputSource( final URL url ) throws IOException {
    this.url = url;
    setURI( url.toString() );
    Reader reader = new BufferedReader( new InputStreamReader( url.openStream() ) );
    setCharacterStream( reader );
  }

  public void close() throws IOException {
    Reader reader = getCharacterStream();
    if ( reader != null ) {
      reader.close();
    }
  }

  public URL getUrl() {
    return url;
  }
}
