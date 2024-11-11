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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data;

import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ParameterAttributeReadHandler extends StringReadHandler {
  private String namespace;
  private String name;

  public ParameterAttributeReadHandler() {
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    name = attrs.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new SAXException( "Required attribute 'name' is missing." );
    }

    namespace = attrs.getValue( getUri(), "namespace" );
    if ( namespace == null ) {
      throw new SAXException( "Required attribute 'namespace' is missing." );
    }
  }

  public String getNamespace() {
    return namespace;
  }

  public String getName() {
    return name;
  }
}
