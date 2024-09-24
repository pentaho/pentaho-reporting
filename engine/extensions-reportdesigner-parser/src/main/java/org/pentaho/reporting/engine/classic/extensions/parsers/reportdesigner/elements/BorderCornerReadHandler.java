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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.DoubleDimensionConverter;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.geom.Dimension2D;

public class BorderCornerReadHandler extends AbstractXmlReadHandler {
  private Dimension2D radius;

  public BorderCornerReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String radiusText = attrs.getValue( getUri(), "radii" );
    radius = DoubleDimensionConverter.getObject( radiusText );
  }

  public Float getWidth() {
    return new Float( radius.getWidth() );
  }

  public Float getHeight() {
    return new Float( radius.getHeight() );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return radius;
  }
}
