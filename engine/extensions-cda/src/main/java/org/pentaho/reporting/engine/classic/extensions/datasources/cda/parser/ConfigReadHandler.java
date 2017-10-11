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

package org.pentaho.reporting.engine.classic.extensions.datasources.cda.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ConfigReadHandler extends AbstractXmlReadHandler {
  private String baseUrl;
  private String baseUrlField;
  private String username;
  private String password;
  private String file;
  private String path;
  private String solution;
  private boolean useLocalCall;
  private boolean sugarMode;

  public ConfigReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    baseUrl = attrs.getValue( getUri(), "base-url" );
    baseUrlField = attrs.getValue( getUri(), "base-url-field" );
    path = attrs.getValue( getUri(), "path" );
    file = attrs.getValue( getUri(), "file" );
    solution = attrs.getValue( getUri(), "solution" );
    username = attrs.getValue( getUri(), "username" );
    password =
      PasswordEncryptionService.getInstance().decrypt( getRootHandler(), attrs.getValue( getUri(), "password" ) );
    useLocalCall = ParserUtil.parseBoolean( attrs.getValue( getUri(), "use-local-call" ), true );
    sugarMode = ParserUtil.parseBoolean( attrs.getValue( getUri(), "is-sugar-mode" ), false );
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getBaseUrlField() {
    return baseUrlField;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getFile() {
    return file;
  }

  public String getPath() {
    return path;
  }

  public String getSolution() {
    return solution;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return null;
  }

  public boolean isUseLocalCall() {
    return useLocalCall;
  }

  public boolean isSugarMode() {
    return sugarMode;
  }
}
