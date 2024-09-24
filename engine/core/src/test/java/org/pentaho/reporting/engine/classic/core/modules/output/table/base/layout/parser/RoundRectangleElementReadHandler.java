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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.parser;

import org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.RectangleElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers.AbstractElementReadHandler;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 20.08.2007, 20:31:53
 *
 * @author Thomas Morgner
 */
public class RoundRectangleElementReadHandler extends AbstractElementReadHandler {
  private RectangleElementFactory factory;

  public RoundRectangleElementReadHandler() {
    factory = new RectangleElementFactory();
  }

  protected ElementFactory getElementFactory() {
    return factory;
  }

  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    super.startParsing( atts );

    final float arcWidth = ParserUtil.parseFloat( atts.getValue( getUri(), "arc-width" ), "No arc-width given." );
    final float arcHeight = ParserUtil.parseFloat( atts.getValue( getUri(), "arc-height" ), "No arc-height given." );

    factory.setName( atts.getValue( getUri(), "id" ) );
    factory.setArcHeight( new Float( arcHeight ) );
    factory.setArcWidth( new Float( arcWidth ) );
    factory.setScale( Boolean.TRUE );
    factory.setKeepAspectRatio( Boolean.FALSE );
    factory.setShouldFill( ParserUtil.parseBoolean( atts.getValue( getUri(), "fill" ), getLocator() ) );
    factory.setShouldDraw( ParserUtil.parseBoolean( atts.getValue( getUri(), "draw" ), getLocator() ) );
    factory.setStroke( ReportParserUtil.parseStroke( atts.getValue( getUri(), "stroke" ), 1 ) );
  }
}
