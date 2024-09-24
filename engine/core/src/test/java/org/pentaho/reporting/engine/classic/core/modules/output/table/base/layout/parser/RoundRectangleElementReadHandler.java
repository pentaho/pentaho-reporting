/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
