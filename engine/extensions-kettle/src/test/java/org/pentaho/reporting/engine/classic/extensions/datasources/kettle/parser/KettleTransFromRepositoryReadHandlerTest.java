/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2024 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class KettleTransFromRepositoryReadHandlerTest {

  private KettleTransFromRepositoryReadHandler kettleTransFromRepositoryReadHandler;

  private Attributes testAttributes;

  @Before
  public void before() throws SAXException {
    ClassicEngineBoot.getInstance().start();
    kettleTransFromRepositoryReadHandler = new KettleTransFromRepositoryReadHandler();
    kettleTransFromRepositoryReadHandler.init( mock( RootXmlReadHandler.class ), "TEST_URI", "TEST_TAG" );

    testAttributes = mock( Attributes.class );
    when( testAttributes.getValue( anyString(), eq( "name" ) ) ).thenReturn( "TEST_NAME" );
    when( testAttributes.getValue( anyString(), eq( "repository" ) ) ).thenReturn( "TEST_REPOSITORY" );
    when( testAttributes.getValue( anyString(), eq( "username" ) ) ).thenReturn( "TEST_USERNAME" );
    when( testAttributes.getValue( anyString(), eq( "password" ) ) ).thenReturn( "TEST_PASSWORD" );
    when( testAttributes.getValue( anyString(), eq( "step" ) ) ).thenReturn( "TEST_STEP" );
  }

  @Test( expected = SAXException.class )
  public void startParsing_exception_on_missed_transformation() throws SAXException {
    kettleTransFromRepositoryReadHandler.startParsing( testAttributes );
  }

  @Test( expected = SAXException.class )
  public void startParsing_exception_on_missed_directory() throws SAXException {
    when( testAttributes.getValue( anyString(), eq( "transformation" ) ) ).thenReturn( "TEST_TRANSFORMATION" );
    kettleTransFromRepositoryReadHandler.startParsing( testAttributes );
  }

  @Test
  public void getObject() throws SAXException {
    when( testAttributes.getValue( anyString(), eq( "transformation" ) ) ).thenReturn( "TEST_TRANSFORMATION" );
    when( testAttributes.getValue( anyString(), eq( "directory" ) ) ).thenReturn( "TEST_DIRECTORY" );
    kettleTransFromRepositoryReadHandler.startParsing( testAttributes );
    kettleTransFromRepositoryReadHandler.doneParsing();
    KettleTransformationProducer actualResult = kettleTransFromRepositoryReadHandler.getObject();
    assertNotNull( actualResult );
    assertEquals( "TEST_STEP", actualResult.getStepName() );
  }

}
