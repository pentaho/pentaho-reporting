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
