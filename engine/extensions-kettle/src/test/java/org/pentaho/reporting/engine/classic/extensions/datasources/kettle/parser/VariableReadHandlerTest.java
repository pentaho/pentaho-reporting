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
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.libraries.formula.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class VariableReadHandlerTest {

  private VariableReadHandler variableReadHandler;

  private Attributes testAttributes;

  @Before
  public void before() throws SAXException {
    ClassicEngineBoot.getInstance().start();
    variableReadHandler = new VariableReadHandler();
    variableReadHandler.init( mock( RootXmlReadHandler.class ), "TEST_URI", "TEST_TAG" );

    testAttributes = mock( Attributes.class );
    when( testAttributes.getValue( anyString(), eq( "name" ) ) ).thenReturn( "TEST_NAME" );
    when( testAttributes.getValue( anyString(), eq( "repository" ) ) ).thenReturn( "TEST_REPOSITORY" );
    when( testAttributes.getValue( anyString(), eq( "username" ) ) ).thenReturn( "TEST_USERNAME" );
    when( testAttributes.getValue( anyString(), eq( "password" ) ) ).thenReturn( "TEST_PASSWORD" );
    when( testAttributes.getValue( anyString(), eq( "step" ) ) ).thenReturn( "TEST_STEP" );
  }

  @Test( expected = SAXException.class )
  public void startParsing_exception_on_missed_formula() throws SAXException {
    variableReadHandler.startParsing( testAttributes );
  }

  @Test( expected = SAXException.class )
  public void startParsing_exception_on_missed_variable_name() throws SAXException {
    when( testAttributes.getValue( anyString(), eq( "formula" ) ) ).thenReturn( "TEST_FORMULA" );
    variableReadHandler.startParsing( testAttributes );
  }

  @Test
  public void getObject() throws SAXException, ParseException {
    when( testAttributes.getValue( anyString(), eq( "datarow-name" ) ) ).thenReturn( "TEST_DATAROW_NAME" );
    variableReadHandler.startParsing( testAttributes );
    FormulaParameter actualResult = variableReadHandler.getObject();
    assertNotNull( actualResult );
    assertEquals( "=[TEST_DATAROW_NAME]", actualResult.getFormula() );
    assertEquals( "TEST_DATAROW_NAME", actualResult.getName() );
  }

}
