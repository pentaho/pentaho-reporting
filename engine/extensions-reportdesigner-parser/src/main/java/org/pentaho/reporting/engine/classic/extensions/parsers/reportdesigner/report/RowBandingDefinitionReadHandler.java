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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report;

import org.pentaho.reporting.engine.classic.core.function.RowBandingFunction;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.ColorConverter;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.*;

public class RowBandingDefinitionReadHandler extends AbstractXmlReadHandler {
  private RowBandingFunction result;

  public RowBandingDefinitionReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );

    final Color color = ColorConverter.getObject( attrs.getValue( getUri(), "color" ) );
    if ( "true".equals( attrs.getValue( getUri(), "enabled" ) ) ) {
      final boolean startState = "true".equals( attrs.getValue( getUri(), "startState" ) );
      final int itemCount = ParserUtil.parseInt
        ( attrs.getValue( getUri(), "switchItemCount" ), "Failed to parse", getLocator() );

      result = new RowBandingFunction();
      result.setVisibleBackground( color );
      result.setInitialState( startState );
      result.setNumberOfElements( itemCount );
    }
  }


  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return result;
  }
}
