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

package org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.parser;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ConfigReadHandler extends AbstractXmlReadHandler {
  private String language;
  private String script;
  private String shutdownScript;

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
    language = attrs.getValue( getUri(), "language" );
    script = attrs.getValue( getUri(), "script" );
    shutdownScript = attrs.getValue( getUri(), "shutdown-script" );
  }

  public String getLanguage() {
    return language;
  }

  public String getScript() {
    return script;
  }

  public String getShutdownScript() {
    return shutdownScript;
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
}
