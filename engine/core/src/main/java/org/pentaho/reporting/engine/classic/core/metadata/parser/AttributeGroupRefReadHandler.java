/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
