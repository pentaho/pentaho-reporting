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
