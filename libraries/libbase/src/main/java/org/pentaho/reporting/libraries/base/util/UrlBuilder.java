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

package org.pentaho.reporting.libraries.base.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility class used to handle some of the messy items when building URLs
 */
public final class UrlBuilder {
  private static final Log log = LogFactory.getLog( UrlBuilder.class );

  private UrlBuilder() {
  }

  /**
   * Generates a URL using the <b>server</b>, <b>path</b>, <b>paramters</b>, and <b>fragment</b> items. The URL will be
   * constructed by basic concatenations (with some glue):<br/> <code>server + path + "?" + params + "#" +
   * fragment</code> Notes: <ul> <li>The method will make sure there is 1-and-only-1 slash between the server and the
   * path</li> <li>The method will perform the proper encoding of the server and path fields</li> <li>If the params
   * string is empty, the '?' will not be added</li> <li>If the fragment is empty, the '#' will not be added</li> </ul>
   *
   * @param server   (required) the server: i.e. <code>"http://source.pentaho.org/"</code>
   * @param path     (required) the path for that server: i.e. <code>"/viewvc/pentaho-reporting"</code>
   * @param params   (optional) any URL parameters to pass (already encoded): i.e. parameter=a+value+with+spaces&x=y
   * @param fragment (optional) any fragment information to be appended to the URL
   * @return the URL generated from the parameters
   * @throws URISyntaxException indicates an error trying to combine the fields
   */
  public static String generateUrl( final String server, final String path, final String params, final String fragment )
    throws URISyntaxException {
    // Validate the parameters
    if ( StringUtils.isEmpty( server ) ) {
      throw new IllegalArgumentException( "The server can not be empty" );
    }
    if ( StringUtils.isEmpty( path ) ) {
      throw new IllegalArgumentException( "The path can not be empty" );
    }

    // Normalize the server
    String normalizedServer = server.trim();
    if ( false == normalizedServer.endsWith( "/" ) ) {
      normalizedServer = normalizedServer + "/";
    }

    // Normalize the path
    String normalizedPath = new URI( null, null, path.trim(), null ).toString();
    while ( '/' == normalizedPath.charAt( 0 ) ) {
      normalizedPath = normalizedPath.substring( 1 );
    }

    // Encode the server and path
    String url = URI.create( normalizedServer + normalizedPath ).toString();

    // Add the parameters and fragment
    if ( false == StringUtils.isEmpty( params, true ) ) {
      url = url + '?' + params.trim();
    }
    if ( false == StringUtils.isEmpty( fragment ) ) {
      url = url + '#' + fragment;
    }

    // For easy debugging
    if ( log.isTraceEnabled() ) {
      log.trace(
        "generateUrl(\"" + server + "\", \"" + path + "\", \"" + params + "\", \"" + fragment + "\") == \"" + url
          + "\"" );
    }
    return url;
  }
}
