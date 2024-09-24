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

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines.xml;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.awt.Color;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineStyleKeys;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SparklineStylesReadHandlerTest {

  private static final String TEST_URI = "test_uri";

  private SparklineStylesReadHandler handler = spy( new SparklineStylesReadHandler() );

  @Test
  public void testStartParsing() throws SAXException {
    ElementStyleSheet styleSheet = mock( ElementStyleSheet.class );
    Attributes attrs = mock( Attributes.class );

    doReturn( TEST_URI ).when( handler ).getUri();

    handler.setStyleSheet( styleSheet );
    assertThat( handler.getObject(), is( instanceOf( ElementStyleSheet.class ) ) );
    assertThat( (ElementStyleSheet) handler.getObject(), is( equalTo( styleSheet ) ) );

    handler.startParsing( attrs );
    verify( styleSheet, never() ).setStyleProperty( any( StyleKey.class ), any() );

    doReturn( "black" ).when( attrs ).getValue( TEST_URI, "high-color" );
    doReturn( "blue" ).when( attrs ).getValue( TEST_URI, "last-color" );
    doReturn( "gray" ).when( attrs ).getValue( TEST_URI, "low-color" );
    doReturn( "yellow" ).when( attrs ).getValue( TEST_URI, "medium-color" );

    handler.startParsing( attrs );
    verify( styleSheet ).setStyleProperty( SparklineStyleKeys.HIGH_COLOR, Color.BLACK );
    verify( styleSheet ).setStyleProperty( SparklineStyleKeys.LAST_COLOR, Color.BLUE );
    verify( styleSheet ).setStyleProperty( SparklineStyleKeys.LOW_COLOR, Color.GRAY );
    verify( styleSheet ).setStyleProperty( SparklineStyleKeys.MEDIUM_COLOR, Color.YELLOW );
  }
}
