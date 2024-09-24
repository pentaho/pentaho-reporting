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

import java.util.Properties;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public interface AuthenticationStore {
  public static final String USER_KEY = "user";
  public static final String PASSWORD_KEY = "password";
  public static final String TIMEOUT_KEY = "timeout";

  public String getUsername( String url );

  public String getPassword( String url );

  public String getOption( final String url, final String key );

  public String[] getDefinedOptions( String url );

  public String[] getKnownURLs();

  public AuthenticationData getCredentials( String url );

  public void add( final AuthenticationData authenticationData, final boolean persist );

  public void addCredentials( final String url,
                              final String user,
                              final String password,
                              final Properties options,
                              final boolean persist );

  public void removeCredentials( final String url );

  public int getIntOption( String path, String key, int defaultValue );

}
