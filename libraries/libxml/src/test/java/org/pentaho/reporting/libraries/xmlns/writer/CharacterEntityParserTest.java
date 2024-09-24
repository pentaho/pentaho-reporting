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

package org.pentaho.reporting.libraries.xmlns.writer;

import junit.framework.TestCase;

public class CharacterEntityParserTest extends TestCase {
  public CharacterEntityParserTest( String s ) {
    super( s );
  }

  public void testEncode() throws Exception {
    final String testNative = "Test is a \u00e4\u00f6\u00fc<&> && test";
    final String testEncoded = "Test is a &auml;&ouml;&uuml;&lt;&amp;&gt; &amp;&amp; test";
    final CharacterEntityParser ep = new CharacterEntityParser( new HtmlCharacterEntities() );
    assertEquals( testNative, ep.decodeEntities( testEncoded ) );
    assertEquals( testEncoded, ep.encodeEntities( testNative ) );
  }
}
