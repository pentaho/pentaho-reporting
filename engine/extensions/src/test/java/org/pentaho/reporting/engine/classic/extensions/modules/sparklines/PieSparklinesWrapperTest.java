/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.modules.sparklines;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.libraries.libsparklines.PieGraphDrawable;

public class PieSparklinesWrapperTest {

  private PieSparklinesWrapper wrapper;
  private PieGraphDrawable sparkline;

  @Before
  public void setUp() {
    sparkline = mock( PieGraphDrawable.class );
    wrapper = new PieSparklinesWrapper( sparkline );
  }

  @Test
  public void testDraw() {
    Graphics2D g2d = mock( Graphics2D.class );
    Rectangle2D bounds = mock( Rectangle2D.class );

    wrapper.draw( g2d, bounds );

    verify( sparkline ).draw( g2d, bounds );
  }

  @Test
  public void testGetPreferredSize() {
    Dimension size = wrapper.getPreferredSize();
    assertThat( size, is( nullValue() ) );
  }

  @Test
  public void testSetStyleSheet() {
    StyleSheet style = null;

    wrapper.setStyleSheet( style );

    verify( sparkline, never() ).setHighColor( any( Color.class ) );
    verify( sparkline, never() ).setBackground( any( Color.class ) );
    verify( sparkline, never() ).setColor( any( Color.class ) );
    verify( sparkline, never() ).setMediumColor( any( Color.class ) );
    verify( sparkline, never() ).setLowColor( any( Color.class ) );
  }

  @Test
  public void testSetStyleSheetWithColor() {
    StyleSheet style = mock( StyleSheet.class );
    doReturn( Color.BLACK ).when( style ).getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
    doReturn( Color.BLUE ).when( style ).getStyleProperty( ElementStyleKeys.PAINT );
    doReturn( Color.RED ).when( style ).getStyleProperty( SparklineStyleKeys.LOW_COLOR );
    doReturn( Color.GRAY ).when( style ).getStyleProperty( SparklineStyleKeys.HIGH_COLOR );
    doReturn( Color.GREEN ).when( style ).getStyleProperty( SparklineStyleKeys.MEDIUM_COLOR );

    wrapper.setStyleSheet( style );

    verify( sparkline ).setMediumColor( Color.GREEN );
    verify( sparkline ).setHighColor( Color.GRAY );
    verify( sparkline ).setBackground( Color.BLACK );
    verify( sparkline ).setColor( Color.BLUE );
    verify( sparkline ).setLowColor( Color.RED );
  }

  @Test
  public void testSetStyleSheetWithNullableColor() {
    StyleSheet style = mock( StyleSheet.class );
    doReturn( null ).when( style ).getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
    doReturn( null ).when( style ).getStyleProperty( ElementStyleKeys.PAINT );
    doReturn( null ).when( style ).getStyleProperty( SparklineStyleKeys.LAST_COLOR );
    doReturn( null ).when( style ).getStyleProperty( SparklineStyleKeys.HIGH_COLOR );

    wrapper.setStyleSheet( style );

    verify( sparkline, never() ).setHighColor( any( Color.class ) );
    verify( sparkline, never() ).setBackground( any( Color.class ) );
    verify( sparkline, never() ).setColor( any( Color.class ) );
    verify( sparkline, never() ).setMediumColor( any( Color.class ) );
    verify( sparkline, never() ).setLowColor( any( Color.class ) );
  }

  @Test
  public void testGetImageMap() {
    assertThat( wrapper.getImageMap( null ), is( nullValue() ) );
  }

  @Test
  public void testIsPreserveAspectRatio() {
    assertThat( wrapper.isPreserveAspectRatio(), is( equalTo( false ) ) );
  }
}
