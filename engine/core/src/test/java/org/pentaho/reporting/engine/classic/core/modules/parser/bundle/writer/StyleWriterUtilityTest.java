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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;

public class StyleWriterUtilityTest {

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testStartParsing() {
    StringWriter stringWriter = new StringWriter();
    XmlWriter writer = new XmlWriter( stringWriter );
    writer.addImpliedNamespace( "http://reporting.pentaho.org/namespaces/engine/classic/bundle/style/1.0", "rep" );
    ElementStyleSheet sheet = new ElementStyleSheet();
    sheet.setBooleanStyleProperty( TextStyleKeys.WORDBREAK, false );
    try {
      StyleWriterUtility.writeTextStyles( writer, sheet );
    } catch (IOException e) {
      e.printStackTrace();
      Assert.fail();
    }
    String res = stringWriter.toString().replace( "\r", "" ).replace( "\n", "" );
    Assert.assertEquals( "<rep:text-styles word-break=\"false\"/>", res );
  }
}
