/*!
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
 * Copyright (c) 2005-2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.output.OutputException;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

public class BarcodeWrapperTest {

  private BarcodeWrapper wrapper;
  private Barcode barcode;
  private Dimension mockedSize;

  @Before
  public void setUp() {
    barcode = mock( Barcode.class );
    mockedSize = mock( Dimension.class );
    doReturn( mockedSize ).when( barcode ).getSize();
    doReturn( mockedSize ).when( barcode ).getPreferredSize();
    wrapper = new BarcodeWrapper( barcode );
  }

  @Test
  public void testDraw() throws OutputException {
    Graphics2D g2d = mock( Graphics2D.class );
    Rectangle2D bounds = mock( Rectangle2D.class );

    doReturn( g2d ).when( g2d ).create();
    doReturn( 200.0 ).when( bounds ).getWidth();
    doReturn( 600.0 ).when( bounds ).getHeight();
    doReturn( 10.0 ).when( bounds ).getX();
    doReturn( 20.0 ).when( bounds ).getY();

    wrapper.draw( g2d, bounds );

    verify( g2d ).clip( bounds );
    verify( g2d, never() ).scale( anyDouble(), anyDouble() );
    verify( barcode ).draw( g2d, 10, 20 );
    verify( g2d ).dispose();
  }

  @Test
  public void testDrawWithScaleAndRatio() throws OutputException {
    Graphics2D g2d = mock( Graphics2D.class );
    Rectangle2D bounds = mock( Rectangle2D.class );
    StyleSheet style = mock( StyleSheet.class );

    doReturn( g2d ).when( g2d ).create();
    doReturn( 200.0 ).when( bounds ).getWidth();
    doReturn( 600.0 ).when( bounds ).getHeight();
    doReturn( 10.0 ).when( bounds ).getX();
    doReturn( 20.0 ).when( bounds ).getY();
    doReturn( true ).when( style ).getBooleanStyleProperty( ElementStyleKeys.SCALE );
    doReturn( true ).when( style ).getBooleanStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO );
    doReturn( 10.0 ).when( mockedSize ).getWidth();
    doReturn( 5.0 ).when( mockedSize ).getHeight();

    wrapper.setStyleSheet( style );
    wrapper.draw( g2d, bounds );

    verify( g2d ).clip( bounds );
    verify( g2d ).scale( 20.0, 20.0 );
    verify( barcode ).draw( g2d, 10, 20 );
    verify( g2d ).dispose();
  }

  @Test
  public void testDrawWithScale() throws OutputException {
    Graphics2D g2d = mock( Graphics2D.class );
    Rectangle2D bounds = mock( Rectangle2D.class );
    StyleSheet style = mock( StyleSheet.class );

    doReturn( g2d ).when( g2d ).create();
    doReturn( 200.0 ).when( bounds ).getWidth();
    doReturn( 600.0 ).when( bounds ).getHeight();
    doReturn( 10.0 ).when( bounds ).getX();
    doReturn( 20.0 ).when( bounds ).getY();
    doReturn( true ).when( style ).getBooleanStyleProperty( ElementStyleKeys.SCALE );
    doReturn( false ).when( style ).getBooleanStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO );
    doReturn( 10.0 ).when( mockedSize ).getWidth();
    doReturn( 5.0 ).when( mockedSize ).getHeight();

    wrapper.setStyleSheet( style );
    wrapper.draw( g2d, bounds );

    verify( g2d ).clip( bounds );
    verify( g2d ).scale( 20.0, 120.0 );
    verify( barcode ).draw( g2d, 10, 20 );
    verify( g2d ).dispose();
  }

  @Test
  public void testGetPreferredSize() {
    Dimension size = wrapper.getPreferredSize();
    assertThat( size, is( equalTo( mockedSize ) ) );
  }

  @Test
  public void testSetStyleSheet() {
    StyleSheet style = null;

    wrapper.setStyleSheet( style );

    verify( barcode, never() ).setFont( any( Font.class ) );
    verify( barcode, never() ).setForeground( any( Color.class ) );
    verify( barcode, never() ).setBackground( any( Color.class ) );
    verify( barcode, never() ).setOpaque( anyBoolean() );

    style = mock( StyleSheet.class );
    wrapper.setStyleSheet( style );

    verify( barcode, never() ).setFont( any( Font.class ) );
    verify( barcode, never() ).setForeground( any( Color.class ) );
    verify( barcode ).setBackground( BarcodeWrapper.ALPHA );
    verify( barcode ).setOpaque( false );

    doReturn( "font_name" ).when( style ).getStyleProperty( TextStyleKeys.FONT );
    doReturn( 14 ).when( style ).getIntStyleProperty( TextStyleKeys.FONTSIZE, 0 );
    doReturn( Color.RED ).when( style ).getStyleProperty( ElementStyleKeys.PAINT );
    doReturn( Color.BLUE ).when( style ).getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );

    wrapper.setStyleSheet( style );

    verify( barcode ).setFont( any( Font.class ) );
    verify( barcode ).setForeground( Color.RED );
    verify( barcode ).setBackground( Color.BLUE );
    verify( barcode ).setOpaque( false );
  }

  @Test
  public void testGetImageMap() {
    assertThat( wrapper.getImageMap( null ), is( nullValue() ) );
  }
}
