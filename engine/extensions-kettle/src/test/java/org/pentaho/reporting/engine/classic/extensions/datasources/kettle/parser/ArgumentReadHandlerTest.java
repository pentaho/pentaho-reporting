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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ArgumentReadHandlerTest {

  private ArgumentReadHandler argumentReadHandler;

  private Attributes testAttributes;

  @Before
  public void before() throws SAXException {
    argumentReadHandler = new ArgumentReadHandler();
    argumentReadHandler.init( mock( RootXmlReadHandler.class ), "TEST_URI", "TEST_TAG" );

    testAttributes = mock( Attributes.class );
    when( testAttributes.getValue( anyString(), eq( "name" ) ) ).thenReturn( "TEST_NAME" );
    when( testAttributes.getValue( anyString(), eq( "repository" ) ) ).thenReturn( "TEST_REPOSITORY" );
    when( testAttributes.getValue( anyString(), eq( "username" ) ) ).thenReturn( "TEST_USERNAME" );
    when( testAttributes.getValue( anyString(), eq( "password" ) ) ).thenReturn( "TEST_PASSWORD" );
    when( testAttributes.getValue( anyString(), eq( "step" ) ) ).thenReturn( "TEST_STEP" );
  }

  @Test
  public void startParsing() throws SAXException {
    when( testAttributes.getValue( anyString(), eq( "formula" ) ) ).thenReturn( "=[TEST_FORMULA]" );
    argumentReadHandler.startParsing( testAttributes );
    assertEquals( FormulaArgument.create( "TEST_FORMULA" ), argumentReadHandler.getFormula() );
  }

  @Test( expected = SAXException.class )
  public void startParsing_exception_on_missed_formula() throws SAXException {
    argumentReadHandler.startParsing( testAttributes );
  }

  @Test
  public void getFormula() throws SAXException {
    when( testAttributes.getValue( anyString(), eq( "datarow-name" ) ) ).thenReturn( "TEST_FORMULA" );
    argumentReadHandler.startParsing( testAttributes );
    FormulaArgument actualFormulaArgument = argumentReadHandler.getFormula();
    assertEquals( "=[TEST_FORMULA]", actualFormulaArgument.getFormula() );
  }

  @Test
  public void getObject() throws SAXException {
    when( testAttributes.getValue( anyString(), eq( "datarow-name" ) ) ).thenReturn( "TEST_FORMULA" );
    argumentReadHandler.startParsing( testAttributes );
    Object actualObject = argumentReadHandler.getObject();
    assertEquals( FormulaArgument.create( "TEST_FORMULA" ), actualObject );
  }

}
