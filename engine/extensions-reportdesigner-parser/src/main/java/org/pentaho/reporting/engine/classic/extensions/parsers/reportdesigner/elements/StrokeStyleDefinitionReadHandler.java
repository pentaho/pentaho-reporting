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

import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.ColorConverter;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.*;

public class StrokeStyleDefinitionReadHandler extends AbstractXmlReadHandler {
  private Color color;
  private Stroke stroke;

  public StrokeStyleDefinitionReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    color = ColorConverter.getObject( attrs.getValue( getUri(), "color" ) );
    final float width =
      ParserUtil.parseFloat( attrs.getValue( getUri(), "width" ), "Failed to parse numeric value", getLocator() );
    final float miterLimit =
      ParserUtil.parseFloat( attrs.getValue( getUri(), "miterlimit" ), "Failed to parse numeric value", getLocator() );
    final float dashPhase =
      ParserUtil.parseFloat( attrs.getValue( getUri(), "dashPhase" ), "Failed to parse numeric value", getLocator() );
    final int cap =
      ParserUtil.parseInt( attrs.getValue( getUri(), "cap" ), "Failed to parse numeric value", getLocator() );
    final int join =
      ParserUtil.parseInt( attrs.getValue( getUri(), "join" ), "Failed to parse numeric value", getLocator() );
    final String dashType = attrs.getValue( getUri(), "dash" );

    float[] dash;
    if ( "DOTTED".equals( dashType ) ) {
      dash = getDotted( width );
    } else if ( "DASHED".equals( dashType ) ) {
      dash = getDashed( width );
    } else if ( "DOT_DASH".equals( dashType ) ) {
      dash = getDotDash( width );
    } else if ( "DOT_DOT_DASH".equals( dashType ) ) {
      dash = getDotDotDash( width );
    } else {
      dash = null;
    }

    stroke = new BasicStroke( width, cap, join, miterLimit, dash, dashPhase );
  }


  private static float[] getDotted( float lineWidth ) {
    return new float[] { 0, 2 * lineWidth };
  }

  private static float[] getDashed( float lineWidth ) {
    return new float[] { 6 * lineWidth, 6 * lineWidth };
  }

  private static float[] getDotDash( float lineWidth ) {
    return new float[] { 0, 2 * lineWidth, 6 * lineWidth, 2 * lineWidth };
  }

  private static float[] getDotDotDash( float lineWidth ) {
    return new float[] { 0, 2 * lineWidth, 0, 2 * lineWidth, 6 * lineWidth, 2 * lineWidth };
  }


  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return null;
  }

  public Color getColor() {
    return color;
  }

  public Stroke getStroke() {
    return stroke;
  }
}
