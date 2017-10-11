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

package org.pentaho.reporting.libraries.docbundle.metadata.parser;

import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.docbundle.metadata.UserDefinedAttribute;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.math.BigDecimal;

public class UserDefinedMetaDataEntryReadHandler extends AbstractXmlReadHandler
  implements BundleMetaDataEntryReadHandler {
  private String name;
  private String valueType;
  private String value;

  public UserDefinedMetaDataEntryReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    name = attrs.getValue( ODFMetaAttributeNames.Meta.NAMESPACE, "name" );
    valueType = attrs.getValue( ODFMetaAttributeNames.Meta.NAMESPACE, "value-type" );
    value = attrs.getValue( ODFMetaAttributeNames.Office.NAMESPACE, "value" );
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    if ( "float".equals( valueType ) ) {
      try {
        return new BigDecimal( value );
      } catch ( NumberFormatException nfe ) {
        try {
          final double val = new Double( value ).doubleValue();
          return new BigDecimal( val );
        } catch ( NumberFormatException nfe2 ) {
          return null;
        }
      }
    } else if ( "date".equals( valueType ) ) {
      return BundleUtilities.parseDate( value );
    } else if ( "time".equals( valueType ) ) {
      return BundleUtilities.parseDuration( value );
    } else if ( "boolean".equals( valueType ) ) {
      if ( value.equals( "true" ) ) {
        return new UserDefinedAttribute( name, Boolean.TRUE );
      }
      return new UserDefinedAttribute( name, Boolean.FALSE );

    } else {
      return new UserDefinedAttribute( name, value );
    }
  }

  public String getMetaDataNameSpace() {
    return getUri();
  }

  public String getMetaDataName() {
    return getTagName();
  }
}
