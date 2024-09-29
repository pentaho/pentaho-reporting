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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class TextStyleReaderHandlerTest {
  private static String URI = "http://reporting.pentaho.org/namespaces/engine/classic/bundle/style/1.0";

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testStartParsing() {
    TextStyleReadHandler handler = Mockito.spy( new TextStyleReadHandler() ) ;
    Mockito.doReturn( URI ).when( handler ).getUri();
    ElementStyleSheet sheet = new ElementStyleSheet();
    handler.setStyleSheet( sheet );
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute( URI,"word-break", "" , "boolean" , "false" );
    try {
      handler.startParsing( attributes );
      Assert.assertFalse( sheet.getBooleanStyleProperty( TextStyleKeys.WORDBREAK ) );
    } catch (SAXException e) {
      Assert.fail();
    }
  }
}
