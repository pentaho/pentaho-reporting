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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AttributeGroupRefReadHandler extends AbstractXmlReadHandler {
  private static final Log logger = LogFactory.getLog( AttributeGroupRefReadHandler.class );
  private AttributeMap<AttributeMetaData> attributes;
  private GlobalMetaDefinition attributeGroups;

  public AttributeGroupRefReadHandler( final AttributeMap<AttributeMetaData> attributes,
      final GlobalMetaDefinition attributeGroups ) {
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( attributeGroups == null ) {
      throw new NullPointerException();
    }

    this.attributes = attributes;
    this.attributeGroups = attributeGroups;
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
    final String name = attrs.getValue( getUri(), "ref" );
    if ( name == null ) {
      throw new ParseException( "Attribute 'ref' is undefined", getLocator() );
    }
    final AttributeGroup group = attributeGroups.getAttributeGroup( name );
    if ( group == null ) {
      logger.debug( "There is no attribute-group '" + name + "' defined. Skipping. " + getLocator() );
      return;
    }

    final AttributeDefinition[] data = group.getMetaData();
    for ( int i = 0; i < data.length; i++ ) {
      final AttributeDefinition handler = data[i];
      final AttributeMetaData metaData = handler.build();
      if ( metaData != null ) {
        attributes.setAttribute( metaData.getNameSpace(), metaData.getName(), metaData );
      }
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
    return null;
  }
}
