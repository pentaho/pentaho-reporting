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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabColumnGroupBodyType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CrosstabColumnSubGroupBodyReadHandler extends AbstractElementReadHandler {
  private CrosstabColumnGroupReadHandler groupColumnReadHandler;

  public CrosstabColumnSubGroupBodyReadHandler() throws ParseException {
    super( CrosstabColumnGroupBodyType.INSTANCE );
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "crosstab-column-group".equals( tagName ) ) {
        groupColumnReadHandler = new CrosstabColumnGroupReadHandler();
        return groupColumnReadHandler;
      }
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  public CrosstabColumnGroupBody getElement() {
    return (CrosstabColumnGroupBody) super.getElement();
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();

    final CrosstabColumnGroupBody body = getElement();
    if ( groupColumnReadHandler != null ) {
      body.setGroup( groupColumnReadHandler.getElement() );
    } else {
      throw new ParseException( "A 'crosstab-column-group' element must be present" );
    }
  }
}
