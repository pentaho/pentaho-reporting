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

package org.pentaho.reporting.engine.classic.core.wizard.parser;

import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeReference;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeReferences;
import org.pentaho.reporting.engine.classic.core.wizard.GlobalRule;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class GlobalRuleReadHandler extends AbstractXmlReadHandler {
  private RuleMetaAttributesReadHandler attributesReadHandler;
  private GlobalRule rule;
  private ArrayList mappings;

  public GlobalRuleReadHandler() {
    this.mappings = new ArrayList();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "data-attributes".equals( tagName ) ) {
        attributesReadHandler = new RuleMetaAttributesReadHandler();
        return attributesReadHandler;
      }
      if ( "data-attribute-mapping".equals( tagName ) ) {
        final DataAttributeMappingReadHandler readHandler = new DataAttributeMappingReadHandler();
        mappings.add( readHandler );
        return readHandler;
      }
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    if ( attributesReadHandler == null ) {
      throw new SAXException( "Mandatory element 'column-attributes' is missing." );
    }
    final DefaultDataAttributeReferences references = new DefaultDataAttributeReferences();
    for ( int i = 0; i < mappings.size(); i++ ) {
      final DataAttributeMappingReadHandler handler = (DataAttributeMappingReadHandler) mappings.get( i );
      references.setReference( handler.getTargetDomain(), handler.getTargetName(), (DataAttributeReference) handler
          .getObject() );
    }
    rule = new GlobalRule( (DataAttributes) attributesReadHandler.getObject(), references );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return rule;
  }
}
