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

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ExpressionReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

public class BulkExpressionReadHandler extends ExpressionReadHandler {
  private String attributeName;
  private String attributeNameSpace;

  public BulkExpressionReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    attributeName = attrs.getValue( BundleNamespaces.LAYOUT, "attribute-name" );
    attributeNameSpace = attrs.getValue( BundleNamespaces.LAYOUT, "attribute-namespace" );

    if ( attributeName == null ) {
      throw new ParseException( "Mandatory attribute 'attribute-name' is missing" );
    }
    if ( attributeNameSpace == null ) {
      throw new ParseException( "Mandatory attribute 'attribute-namespace' is missing" );
    }
    super.startParsing( attrs );
  }

  public String getAttributeName() {
    return attributeName;
  }

  public String getAttributeNameSpace() {
    return attributeNameSpace;
  }
}
