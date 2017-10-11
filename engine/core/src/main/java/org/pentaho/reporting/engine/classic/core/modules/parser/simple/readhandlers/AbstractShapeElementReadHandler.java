/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import java.awt.Stroke;

import org.pentaho.reporting.engine.classic.core.elementfactory.AbstractContentElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

public abstract class AbstractShapeElementReadHandler extends AbstractElementReadHandler {
  private static final String SCALE_ATT = "scale";
  private static final String KEEP_ASPECT_RATIO_ATT = "keepAspectRatio";
  private static final String FILL_ATT = "fill";
  private static final String DRAW_ATT = "draw";

  protected AbstractShapeElementReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param atts
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    super.startParsing( atts );
    handleScale( atts );
    handleKeepAspectRatio( atts );
    handleFill( atts );
    handleDraw( atts );
    handleStroke( atts );
  }

  private void handleStroke( final PropertyAttributes atts ) throws ParseException {
    final String strokeStyle = atts.getValue( getUri(), "stroke-style" );

    final String weightAttr = atts.getValue( getUri(), "weight" );
    float weight = 1;
    if ( weightAttr != null ) {
      weight = ParserUtil.parseFloat( weightAttr, "Weight is given, but no number.", getLocator() );
    }

    final Stroke stroke = ReportParserUtil.parseStroke( strokeStyle, weight );

    if ( stroke != null ) {
      final AbstractContentElementFactory elementFactory = (AbstractContentElementFactory) getElementFactory();
      elementFactory.setStroke( stroke );
    }
  }

  protected void handleScale( final PropertyAttributes atts ) throws ParseException {
    final String booleanValue = atts.getValue( getUri(), AbstractShapeElementReadHandler.SCALE_ATT );
    final AbstractContentElementFactory elementFactory = (AbstractContentElementFactory) getElementFactory();
    final Boolean scale = ParserUtil.parseBoolean( booleanValue, getLocator() );
    if ( scale != null ) {
      elementFactory.setScale( scale );
    }
  }

  protected void handleKeepAspectRatio( final PropertyAttributes atts ) throws ParseException {
    final String booleanValue = atts.getValue( getUri(), AbstractShapeElementReadHandler.KEEP_ASPECT_RATIO_ATT );
    final AbstractContentElementFactory elementFactory = (AbstractContentElementFactory) getElementFactory();
    final Boolean keepAspectRatio = ParserUtil.parseBoolean( booleanValue, getLocator() );
    if ( keepAspectRatio != null ) {
      elementFactory.setKeepAspectRatio( keepAspectRatio );
    }
  }

  protected void handleFill( final PropertyAttributes atts ) throws ParseException {
    final String booleanValue = atts.getValue( getUri(), AbstractShapeElementReadHandler.FILL_ATT );
    final AbstractContentElementFactory elementFactory = (AbstractContentElementFactory) getElementFactory();
    final Boolean fill = ParserUtil.parseBoolean( booleanValue, getLocator() );
    if ( fill != null ) {
      elementFactory.setShouldFill( fill );
    }
  }

  protected void handleDraw( final PropertyAttributes atts ) throws ParseException {
    final String booleanValue = atts.getValue( getUri(), AbstractShapeElementReadHandler.DRAW_ATT );
    final AbstractContentElementFactory elementFactory = (AbstractContentElementFactory) getElementFactory();
    final Boolean draw = ParserUtil.parseBoolean( booleanValue, getLocator() );
    if ( draw != null ) {
      elementFactory.setShouldDraw( draw );
    }
  }

}
