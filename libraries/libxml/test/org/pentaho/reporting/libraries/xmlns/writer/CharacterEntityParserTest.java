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
* Copyright (c) 2000 - 2009 Pentaho Corporation, Simba Management Limited and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.writer;

import junit.framework.TestCase;

public class CharacterEntityParserTest extends TestCase
{
  public CharacterEntityParserTest (String s)
  {
    super(s);
  }

  public void testEncode () throws Exception
  {
    final String testNative = "Test is a \u00e4\u00f6\u00fc<&> && test";
    final String testEncoded = "Test is a &auml;&ouml;&uuml;&lt;&amp;&gt; &amp;&amp; test";
    final CharacterEntityParser ep = new CharacterEntityParser (new HtmlCharacterEntities());
    assertEquals(testNative, ep.decodeEntities(testEncoded));
    assertEquals(testEncoded, ep.encodeEntities(testNative));
  }
}
