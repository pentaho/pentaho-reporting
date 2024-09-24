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
