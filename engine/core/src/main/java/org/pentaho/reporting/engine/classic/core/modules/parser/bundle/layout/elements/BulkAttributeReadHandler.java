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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BulkAttributeReadHandler extends StringReadHandler {
  private String namespace;
  private String name;
  private ReportAttributeMap<String> attributes;

  public BulkAttributeReadHandler( final String namespace, final String name ) {
    if ( namespace == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.namespace = namespace;
    this.name = name;
    this.attributes = new ReportAttributeMap<String>();
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    final int length = attrs.getLength();
    for ( int i = 0; i < length; i++ ) {
      attributes.setAttribute( attrs.getURI( i ), attrs.getLocalName( i ), attrs.getValue( i ) );
    }
  }

  public String getNamespace() {
    return namespace;
  }

  public String getName() {
    return name;
  }

  public ReportAttributeMap<String> getAttributes() {
    return attributes;
  }
}
