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


package org.pentaho.reporting.engine.classic.core.wizard.parser;

import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributes;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RuleMetaAttributesReadHandler extends AbstractXmlReadHandler {
  private DefaultDataAttributes dataAttributes;

  public RuleMetaAttributesReadHandler() {
    this.dataAttributes = new DefaultDataAttributes();
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final int length = attrs.getLength();
    for ( int i = 0; i < length; i++ ) {
      final String name = attrs.getLocalName( i );
      final String namespace = attrs.getURI( i );
      final String attributeValue = attrs.getValue( i );
      dataAttributes.setMetaAttribute( namespace, name, DefaultConceptQueryMapper.INSTANCE, attributeValue );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return dataAttributes;
  }
}
