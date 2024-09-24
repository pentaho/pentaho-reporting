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

package org.pentaho.reporting.libraries.xmlns.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * The Property-ReadHandler reads character data from an element along with a 'name' attribute. This way, this class is
 * most suitable for creating name-value-pairs.
 *
 * @author Thomas Morgner
 */
public class PropertyReadHandler extends StringReadHandler {
  private String name;
  private String nameAttribute;
  private boolean nameMandatory;

  /**
   * The Default-Constructor.
   */
  public PropertyReadHandler() {
    nameAttribute = "name";
    nameMandatory = true;
  }

  public PropertyReadHandler( final String name, final boolean nameMandatory ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    this.nameAttribute = name;
    this.nameMandatory = nameMandatory;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    name = attrs.getValue( getUri(), nameAttribute );
    if ( nameMandatory && name == null ) {
      throw new ParseException( "Required attribute '" + nameAttribute + "' missing", getLocator() );
    }
  }

  /**
   * Returns the declared property-name.
   *
   * @return the property name.
   */
  public String getName() {
    return name;
  }
}
