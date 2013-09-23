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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.writer;

import java.io.StringWriter;
import java.io.IOException;

import junit.framework.TestCase;

public class XmlWriterSupportTest extends TestCase
{
  public XmlWriterSupportTest()
  {
  }

  public XmlWriterSupportTest(final String s)
  {
    super(s);
  }

  public void testEncoding() throws IOException
  {
    final StringWriter writer1 = new StringWriter();
    final StringWriter writer2 = new StringWriter();
    final StringWriter writer3 = new StringWriter();
    final StringWriter writer4 = new StringWriter();
    final StringWriter writer5 = new StringWriter();
    final StringWriter writer6 = new StringWriter();

    final XmlWriterSupport support = new XmlWriterSupport(new DefaultTagDescription(), "");
    support.writeTextNormalized(writer1, "Some text to make me happy", false);
    support.writeTextNormalized(writer2, "Some <text> &to; make me happy", false);
    support.writeTextNormalized(writer3, "Some <<text to >>make me happy", false);
    support.writeTextNormalized(writer4, "Some \n>text to <\rmake me happy", false);
    support.writeTextNormalized(writer5, "Some \n>text to <\rmake me happy", true);
    support.writeTextNormalized(writer6, "Some \\d>text to \\windows\\path <\rmake me happy", true);

    assertEquals(writer1.toString(), "Some text to make me happy");
    assertEquals(writer2.toString(), "Some &lt;text&gt; &amp;to; make me happy");
    assertEquals(writer3.toString(), "Some &lt;&lt;text to &gt;&gt;make me happy");
    assertEquals(writer4.toString(), "Some \n" +
        "&gt;text to &lt;\r" +
        "make me happy");
    assertEquals(writer5.toString(), "Some &#x000a;&gt;text to &lt;&#x000d;make me happy");
    assertEquals(writer6.toString(), "Some \\d&gt;text to \\windows\\path &lt;&#x000d;make me happy");
  }
}
