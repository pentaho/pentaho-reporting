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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RotationStyleReadHandler extends AbstractXmlReadHandler implements StyleReadHandler {

  private ElementStyleSheet styleSheet;

  public RotationStyleReadHandler() {
  }

  public ElementStyleSheet getStyleSheet() {
    return styleSheet;
  }

  public void setStyleSheet( final ElementStyleSheet styleSheet ) {
    this.styleSheet = styleSheet;
  }


  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String rotation = attrs.getValue( getUri(), "rotation" );
    if ( rotation != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.TEXT_ROTATION, parseTextRotation( rotation ) );
    }
  }

  private TextRotation parseTextRotation( final String attr ) {

    if ( TextRotation.D_90.toString().equalsIgnoreCase( attr ) ) {
      return TextRotation.D_90;
    }

    if ( TextRotation.D_270.toString().equalsIgnoreCase( attr ) ) {
      return TextRotation.D_270;
    }

    return null;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return styleSheet;
  }
}
