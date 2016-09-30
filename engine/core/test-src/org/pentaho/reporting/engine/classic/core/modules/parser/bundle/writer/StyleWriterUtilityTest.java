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
 *  Copyright (c) 2006 - 2016 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

/**
 * @author Andrei Abramov
 */
public class StyleWriterUtilityTest {
  @Test
  public void writeBorderStyles() throws Exception {

    XmlWriter writer = mock( XmlWriter.class );
    ElementStyleSheet style = mock( ElementStyleSheet.class );
    FastDecimalFormat absoluteLengthFormat = new FastDecimalFormat( "0.###", Locale.US );
    ArgumentCaptor<AttributeList> attrsCaptor = ArgumentCaptor.forClass( AttributeList.class );

    verify( writer, never() ).writeTag( anyString(), anyString(), any( AttributeList.class ), anyBoolean() );

    doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH );
    doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH );
    doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH );
    doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH );
    doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT );
    doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT );
    doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT );
    doReturn( true ).when( style ).isLocalKey( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT );

    doReturn( 15.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, 0 );
    doReturn( 16.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, 0 );
    doReturn( 17.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, 0 );
    doReturn( 18.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, 0 );
    doReturn( 15.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, 0 );
    doReturn( 16.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, 0 );
    doReturn( 17.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, 0 );
    doReturn( 18.0 ).when( style ).getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, 0 );

    doNothing().when( writer ).writeTag( anyString(), anyString(), attrsCaptor.capture(), anyBoolean() );

    StyleWriterUtility.writeBorderStyles( writer, style );

    verify( writer ).writeTag( anyString(), anyString(), any( AttributeList.class ), anyBoolean() );

    assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-top-left-radius-width" ),
      is( equalTo( absoluteLengthFormat.format( 15.0 ) ) ) );
    assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-top-right-radius-width" ),
      is( equalTo( absoluteLengthFormat.format( 16.0 ) ) ) );
    assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-bottom-left-radius-width" ),
      is( equalTo( absoluteLengthFormat.format( 17.0 ) ) ) );
    assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-bottom-right-radius-width" ),
      is( equalTo( absoluteLengthFormat.format( 18.0 ) ) ) );
    assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-top-left-radius-height" ),
      is( equalTo( absoluteLengthFormat.format( 15.0 ) ) ) );
    assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-top-right-radius-height" ),
      is( equalTo( absoluteLengthFormat.format( 16.0 ) ) ) );
    assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-bottom-left-radius-height" ),
      is( equalTo( absoluteLengthFormat.format( 17.0 ) ) ) );
    assertThat( attrsCaptor.getValue().getAttribute( BundleNamespaces.STYLE, "border-bottom-right-radius-height" ),
      is( equalTo( absoluteLengthFormat.format( 18.0 ) ) ) );
  }

}