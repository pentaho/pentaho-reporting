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


package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.xml.sax.SAXException;

public class RoundRectangleReadHandler extends AbstractShapeElementReadHandler {
  private static final String ARC_WIDTH_ATT = "arc-height";
  private static final String ARC_HEIGHT_ATT = "arc-width";
  private RectangleElementFactory elementFactory;

  public RoundRectangleReadHandler() {
    elementFactory = new RectangleElementFactory();
  }

  /**
   * Starts parsing.
   *
   * @param atts
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    // these are local defaults ...
    elementFactory.setShouldDraw( Boolean.TRUE );
    elementFactory.setShouldFill( Boolean.TRUE );

    super.startParsing( atts );
    elementFactory.setScale( Boolean.TRUE );
    elementFactory.setDynamicHeight( Boolean.FALSE );
    elementFactory.setKeepAspectRatio( Boolean.FALSE );

    final Float arcWidth =
        ReportParserUtil.parseFloat( atts.getValue( getUri(), RoundRectangleReadHandler.ARC_WIDTH_ATT ), getLocator() );
    final Float arcHeight =
        ReportParserUtil.parseFloat( atts.getValue( getUri(), RoundRectangleReadHandler.ARC_HEIGHT_ATT ), getLocator() );
    elementFactory.setArcHeight( arcHeight );
    elementFactory.setArcWidth( arcWidth );
  }

  protected ElementFactory getElementFactory() {
    return elementFactory;
  }
}
