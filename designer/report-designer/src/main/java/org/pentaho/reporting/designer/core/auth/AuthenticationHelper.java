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

package org.pentaho.reporting.designer.core.auth;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public class AuthenticationHelper {
  private static final char DOMAIN_SEPARATOR = '\\';
  private static final String NT_AUTH_CONFIGKEY =
    "org.pentaho.reporting.designer.core.auth.AllowNtDomainAuthentication";

  private AuthenticationHelper() {
  }

  public static Credentials getCredentials( final String user,
                                           final String password ) {
    if ( StringUtils.isEmpty( user ) ) {
      return null;
    }

    final Configuration config = ReportDesignerBoot.getInstance().getGlobalConfig();
    if ( "true".equals( config.getConfigProperty( NT_AUTH_CONFIGKEY, "false" ) ) == false ) {
      return new UsernamePasswordCredentials( user, password );
    }

    final int domainIdx = user.indexOf( DOMAIN_SEPARATOR );
    if ( domainIdx == -1 ) {
      return new UsernamePasswordCredentials( user, password );
    }
    try {
      final String domain = user.substring( 0, domainIdx );
      final String username = user.substring( domainIdx + 1 );
      final String host = InetAddress.getLocalHost().getHostName();
      return new NTCredentials( username, password, host, domain );
    } catch ( UnknownHostException uhe ) {
      return new UsernamePasswordCredentials( user, password );
    }
  }

  public static Credentials getCredentials( final String url,
                                            final AuthenticationStore store ) {
    final String user = store.getUsername( url );
    if ( user == null ) {
      return null;
    }

    final String password = store.getPassword( url );

    final Configuration config = ReportDesignerBoot.getInstance().getGlobalConfig();
    if ( "true".equals( config.getConfigProperty( NT_AUTH_CONFIGKEY, "false" ) ) == false ) {
      return new UsernamePasswordCredentials( user, password );
    }

    final int domainIdx = user.indexOf( DOMAIN_SEPARATOR );
    if ( domainIdx == -1 ) {
      return new UsernamePasswordCredentials( user, password );
    }
    try {
      final String domain = user.substring( 0, domainIdx );
      final String username = user.substring( domainIdx + 1 );
      final String host = InetAddress.getLocalHost().getHostName();
      return new NTCredentials( username, password, host, domain );
    } catch ( UnknownHostException uhe ) {
      return new UsernamePasswordCredentials( user, password );
    }
  }
}
