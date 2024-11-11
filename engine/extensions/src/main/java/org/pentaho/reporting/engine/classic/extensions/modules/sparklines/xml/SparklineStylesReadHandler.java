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


package org.pentaho.reporting.engine.classic.extensions.modules.sparklines.xml;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.StyleReadHandler;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineStyleKeys;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SparklineStylesReadHandler extends AbstractXmlReadHandler implements StyleReadHandler {
  private ElementStyleSheet styleSheet;

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String highColor = attrs.getValue( getUri(), "high-color" );
    if ( highColor != null ) {
      styleSheet.setStyleProperty( SparklineStyleKeys.HIGH_COLOR, ReportParserUtil.parseColor( highColor, null ) );
    }

    final String lastColor = attrs.getValue( getUri(), "last-color" );
    if ( lastColor != null ) {
      styleSheet.setStyleProperty( SparklineStyleKeys.LAST_COLOR, ReportParserUtil.parseColor( lastColor, null ) );
    }

    final String lowColor = attrs.getValue( getUri(), "low-color" );
    if ( lastColor != null ) {
      styleSheet.setStyleProperty( SparklineStyleKeys.LOW_COLOR, ReportParserUtil.parseColor( lowColor, null ) );
    }

    final String mediumColor = attrs.getValue( getUri(), "medium-color" );
    if ( lastColor != null ) {
      styleSheet.setStyleProperty( SparklineStyleKeys.MEDIUM_COLOR, ReportParserUtil.parseColor( mediumColor, null ) );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occurred.
   */
  public Object getObject() throws SAXException {
    return styleSheet;
  }

  public void setStyleSheet( final ElementStyleSheet styleSheet ) {
    this.styleSheet = styleSheet;
  }
}
