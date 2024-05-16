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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

public class BarcodeDrawableTest {

  private static final String MSG = "test msg";

  private BarcodeDrawable barcodeDrawable;
  private BarcodeGenerator generator;
  private BarcodeDimension barcodeDimension;

  @Before
  public void setUp() {
    generator = mock( BarcodeGenerator.class );
    barcodeDimension = mock( BarcodeDimension.class );
    doReturn( barcodeDimension ).when( generator ).calcDimensions( MSG );
    barcodeDrawable = new BarcodeDrawable( generator, MSG );
  }

  @Test
  public void testDraw() {
    Graphics2D g2d = mock( Graphics2D.class );
    Rectangle2D bounds = mock( Rectangle2D.class );

    doReturn( 200.0 ).when( bounds ).getWidth();
    doReturn( 600.0 ).when( bounds ).getHeight();
    doReturn( 10.0 ).when( barcodeDimension ).getWidthPlusQuiet();
    doReturn( 20.0 ).when( barcodeDimension ).getHeightPlusQuiet();

    barcodeDrawable.draw( g2d, bounds );

    verify( g2d ).scale( 20, 20 );
    verify( g2d ).translate( 0.0, 5.0 );
    verify( g2d ).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    verify( g2d ).setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
    verify( g2d, never() ).setFont( any( Font.class ) );
    verify( g2d, never() ).setColor( any( Color.class ) );
    verify( g2d, never() ).fill( bounds );
    verify( generator ).generateBarcode( any( CanvasProvider.class ), anyString() );
  }

  @Test
  public void testDrawWithStyling() {
    Graphics2D g2d = mock( Graphics2D.class );
    Rectangle2D bounds = mock( Rectangle2D.class );
    StyleSheet style = mock( StyleSheet.class );

    doReturn( 600.0 ).when( bounds ).getWidth();
    doReturn( 200.0 ).when( bounds ).getHeight();
    doReturn( 10.0 ).when( barcodeDimension ).getWidthPlusQuiet();
    doReturn( 20.0 ).when( barcodeDimension ).getHeightPlusQuiet();

    doReturn( "font_name" ).when( style ).getStyleProperty( TextStyleKeys.FONT );
    doReturn( 14 ).when( style ).getIntStyleProperty( TextStyleKeys.FONTSIZE, 0 );
    doReturn( Color.RED ).when( style ).getStyleProperty( ElementStyleKeys.PAINT );
    doReturn( Color.BLUE ).when( style ).getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
    doReturn( true ).when( style ).getBooleanStyleProperty( TextStyleKeys.BOLD );
    doReturn( true ).when( style ).getBooleanStyleProperty( TextStyleKeys.ITALIC );

    barcodeDrawable.setStyleSheet( style );
    barcodeDrawable.draw( g2d, bounds );

    verify( g2d ).scale( 10, 10 );
    verify( g2d ).translate( 25.0, 0.0 );
    verify( g2d ).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    verify( g2d ).setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
    verify( g2d ).setFont( any( Font.class ) );
    verify( g2d ).setColor( Color.RED );
    verify( g2d ).setColor( Color.BLUE );
    verify( g2d ).fill( bounds );
    verify( generator ).generateBarcode( any( CanvasProvider.class ), anyString() );
  }

  @Test
  public void testGetImageMap() {
    assertThat( barcodeDrawable.getImageMap( null ), is( nullValue() ) );
  }

}
