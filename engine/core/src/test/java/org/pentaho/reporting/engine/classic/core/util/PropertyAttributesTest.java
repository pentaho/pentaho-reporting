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
