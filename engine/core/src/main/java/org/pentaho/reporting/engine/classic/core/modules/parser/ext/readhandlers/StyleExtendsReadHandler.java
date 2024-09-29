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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

import java.util.HashMap;

public class StyleExtendsReadHandler extends AbstractPropertyXmlReadHandler {
  private HashMap<String, ElementStyleSheet> styleSheetCollection;
  private ElementStyleSheet styleSheet;

  public StyleExtendsReadHandler( final HashMap<String, ElementStyleSheet> styleSheetCollection,
      final ElementStyleSheet styleSheet ) {
    this.styleSheetCollection = styleSheetCollection;
    this.styleSheet = styleSheet;
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
    final String name = attrs.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new ParseException( "Required attribute 'name' is missing.", getRootHandler().getDocumentLocator() );
    }
    final ElementStyleSheet parent = styleSheetCollection.get( name );
    if ( parent == null ) {
      throw new ParseException( "Specified parent stylesheet is not defined.", getRootHandler().getDocumentLocator() );
    }
    styleSheet.addDefault( parent );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return null;
  }
}
