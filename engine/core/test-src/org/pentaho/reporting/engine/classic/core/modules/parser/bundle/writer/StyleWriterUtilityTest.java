/*
 * This program CoreMatchers.is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program CoreMatchers.is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import org.junit.Assert;
import org.hamcrest.CoreMatchers;
import org.mockito.Mockito;

/**
 * @author Andrei Abramov
 */
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
    } catch ( IOException e ) {
      e.printStackTrace();
      Assert.fail();
    }
    String res = stringWriter.toString().replace( "\r", "" ).replace( "\n", "" );
    Assert.assertEquals( "<rep:text-styles word-break=\"false\"/>", res );
  }

  @Test
  public void writeBorderStyles() throws Exception {

    XmlWriter writer = Mockito.mock( XmlWriter.class );
    ElementStyleSheet style = Mockito.mock( ElementStyleSheet.class );
    FastDecimalFormat absoluteLengthFormat = new FastDecimalFormat( "0.###", Locale.US );
    ArgumentCaptor<AttributeList> attrsCaptor = ArgumentCaptor.forClass( AttributeList.class );

    Mockito.verify( writer, Mockito.never() )
      .writeTag( Mockito.anyString(), Mockito.anyString(), Mockito.any( AttributeList.class ), Mockito.anyBoolean() );

    Mockito.doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH );
    Mockito.doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH );
    Mockito.doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH );
    Mockito.doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH );
    Mockito.doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT );
    Mockito.doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT );
    Mockito.doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT );
    Mockito.doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT );

    Mockito.doReturn( 15.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, 0 );
    Mockito.doReturn( 16.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, 0 );
    Mockito.doReturn( 17.0 ).when( style )
      .getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, 0 );
    Mockito.doReturn( 18.0 ).when( style )
      .getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, 0 );
    Mockito.doReturn( 15.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, 0 );
    Mockito.doReturn( 16.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, 0 );
    Mockito.doReturn( 17.0 ).when( style )
      .getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, 0 );
    Mockito.doReturn( 18.0 ).when( style )
      .getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, 0 );

    Mockito.doNothing().when( writer )
      .writeTag( Mockito.anyString(), Mockito.anyString(), attrsCaptor.capture(), Mockito.anyBoolean() );

    StyleWriterUtility.writeBorderStyles( writer, style );

    Mockito.verify( writer )
      .writeTag( Mockito.anyString(), Mockito.anyString(), Mockito.any( AttributeList.class ), Mockito.anyBoolean() );

    Assert.assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-top-left-radius-width" ),
      CoreMatchers.is( CoreMatchers.equalTo( absoluteLengthFormat.format( 15.0 ) ) ) );
    Assert.assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-top-right-radius-width" ),
      CoreMatchers.is( CoreMatchers.equalTo( absoluteLengthFormat.format( 16.0 ) ) ) );
    Assert.assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-bottom-left-radius-width" ),
      CoreMatchers.is( CoreMatchers.equalTo( absoluteLengthFormat.format( 17.0 ) ) ) );
    Assert
      .assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-bottom-right-radius-width" ),
        CoreMatchers.is( CoreMatchers.equalTo( absoluteLengthFormat.format( 18.0 ) ) ) );
    Assert.assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-top-left-radius-height" ),
      CoreMatchers.is( CoreMatchers.equalTo( absoluteLengthFormat.format( 15.0 ) ) ) );
    Assert.assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-top-right-radius-height" ),
      CoreMatchers.is( CoreMatchers.equalTo( absoluteLengthFormat.format( 16.0 ) ) ) );
    Assert
      .assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-bottom-left-radius-height" ),
        CoreMatchers.is( CoreMatchers.equalTo( absoluteLengthFormat.format( 17.0 ) ) ) );
    Assert
      .assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-bottom-right-radius-height" ),
        CoreMatchers.is( CoreMatchers.equalTo( absoluteLengthFormat.format( 18.0 ) ) ) );
  }

}
