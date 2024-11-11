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
