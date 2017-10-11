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

package org.pentaho.reporting.engine.classic.core.util;

import junit.framework.TestCase;

import java.util.HashMap;

public class PropertyAttributesTest extends TestCase {
  private class StringLookupParser extends PropertyLookupParser {
    private HashMap rootXmlHandler;

    public StringLookupParser( final HashMap rootXmlHandler ) {
      this.rootXmlHandler = rootXmlHandler;
    }

    /**
     * Looks up the property with the given name.
     *
     * @param property
     *          the name of the property to look up.
     * @return the translated value.
     */
    protected String lookupVariable( final String property ) {
      return String.valueOf( rootXmlHandler.get( property ) );
    }
  }

  public PropertyAttributesTest( final String s ) {
    super( s );
  }

  public void testSimple() {
    HashMap rootXmlReadHandler = new HashMap();
    rootXmlReadHandler.put( "property", "ARRGH" );
    rootXmlReadHandler.put( "property2", ".." );

    final StringLookupParser parser = new StringLookupParser( rootXmlReadHandler );
    final String result = parser.translateAndLookup( "${property}" );
    assertEquals( "ARRGH", result );

    final String result2 = parser.translateAndLookup( "${property}${property2}" );
    assertEquals( "ARRGH..", result2 );
  }

  public void testEscapes() {
    HashMap rootXmlReadHandler = new HashMap();
    rootXmlReadHandler.put( "property", "ARRGH" );
    rootXmlReadHandler.put( "property$", ".." );

    final StringLookupParser parser = new StringLookupParser( rootXmlReadHandler );

    final String result = parser.translateAndLookup( "${\\property}" );
    assertEquals( "ARRGH", result );

    final String result2 = parser.translateAndLookup( "${property}\\$${property\\$}" );
    assertEquals( "ARRGH$..", result2 );
  }

  public void testEvilUserString() {
    HashMap rootXmlReadHandler = new HashMap();
    rootXmlReadHandler.put( "property", "ARRGH" );
    rootXmlReadHandler.put( "property$", ".." );

    final StringLookupParser parser = new StringLookupParser( rootXmlReadHandler );

    final String result = parser.translateAndLookup( "$\\{\\property}" );
    assertEquals( "${\\property}", result );

    final String result2 = parser.translateAndLookup( "\\${property}\\$${property\\$}" );
    assertEquals( "${property}$..", result2 );
  }
}
