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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineModule.NAMESPACE;

import java.awt.Color;
import java.io.IOException;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineStyleKeys;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

public class SparklineStyleSetWriteHandlerTest {

  private SparklineStyleSetWriteHandler handler = new SparklineStyleSetWriteHandler();

  @Test
  public void testWriteStyle() throws IOException {
    XmlWriter writer = mock( XmlWriter.class );
    ElementStyleSheet style = mock( ElementStyleSheet.class );

    handler.writeStyle( writer, style );

    verify( writer, never() ).writeTag( anyString(), anyString(), any( AttributeList.class ), anyBoolean() );

    doReturn( true ).when( style ).isLocalKey( SparklineStyleKeys.HIGH_COLOR );
    doReturn( true ).when( style ).isLocalKey( SparklineStyleKeys.MEDIUM_COLOR );
    doReturn( true ).when( style ).isLocalKey( SparklineStyleKeys.LOW_COLOR );
    doReturn( true ).when( style ).isLocalKey( SparklineStyleKeys.LAST_COLOR );

    doReturn( Color.BLACK ).when( style ).getStyleProperty( SparklineStyleKeys.HIGH_COLOR );
    doReturn( Color.BLUE ).when( style ).getStyleProperty( SparklineStyleKeys.MEDIUM_COLOR );
    doReturn( Color.GRAY ).when( style ).getStyleProperty( SparklineStyleKeys.LOW_COLOR );
    doReturn( Color.YELLOW ).when( style ).getStyleProperty( SparklineStyleKeys.LAST_COLOR );

    ArgumentCaptor<AttributeList> attrsCaptor = ArgumentCaptor.forClass( AttributeList.class );
    doNothing().when( writer ).writeTag( anyString(), anyString(), attrsCaptor.capture(), anyBoolean() );

    handler.writeStyle( writer, style );

    verify( writer ).writeTag( anyString(), anyString(), any( AttributeList.class ), anyBoolean() );
    assertThat( attrsCaptor.getValue().getAttribute( NAMESPACE, "high-color" ), is( equalTo( "black" ) ) );
    assertThat( attrsCaptor.getValue().getAttribute( NAMESPACE, "medium-color" ), is( equalTo( "blue" ) ) );
    assertThat( attrsCaptor.getValue().getAttribute( NAMESPACE, "low-color" ), is( equalTo( "gray" ) ) );
    assertThat( attrsCaptor.getValue().getAttribute( NAMESPACE, "last-color" ), is( equalTo( "yellow" ) ) );
  }
}
