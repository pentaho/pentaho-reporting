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
* Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.writer;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringWriter;

public class XmlWriterSupportTest extends TestCase {

  private final XmlWriterSupport support = new XmlWriterSupport( new DefaultTagDescription(), "" );

  public XmlWriterSupportTest() {
  }

  public void testEncodings() throws IOException {
    testEncoding( "Some text to make me happy", "Some text to make me happy" );
    testEncoding( "Some <text> &to; make me happy", "Some &lt;text&gt; &amp;to; make me happy" );
    testEncoding( "Some <<text to >>make me happy", "Some &lt;&lt;text to &gt;&gt;make me happy" );
    testEncoding( "Some \n>text to <\rmake me happy\t\0", "Some \n&gt;text to &lt;\rmake me happy\t" );
    testEncoding( "Some \\d>text to \\windows\\path <\rmake me happy",
        "Some \\d&gt;text to \\windows\\path &lt;&#13;make me happy", true );

    testEncoding( "\uD842\uDFB7", "\uD842\uDFB7" );

    support.setEncoding( "cp1251" );
    testEncoding( "\uD842\uDFB7", "&#x20bb7" );
  }

  private void testEncoding( final String before, final String after ) throws IOException {
    testEncoding( before, after, false );
  }

  private void testEncoding( final String before, final String after, final boolean transformNewLine )
      throws IOException {
    final StringWriter writer = new StringWriter();
    support.writeTextNormalized( writer, before, transformNewLine );
    assertEquals( after, writer.toString() );
  }
}
