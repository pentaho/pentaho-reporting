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
