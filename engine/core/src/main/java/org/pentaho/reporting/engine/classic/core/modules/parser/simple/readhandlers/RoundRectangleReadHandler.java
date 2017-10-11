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
