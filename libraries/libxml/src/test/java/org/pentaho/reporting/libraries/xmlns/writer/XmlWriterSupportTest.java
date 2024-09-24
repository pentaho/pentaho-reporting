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
