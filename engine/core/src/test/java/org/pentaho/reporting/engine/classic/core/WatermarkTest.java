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

package org.pentaho.reporting.engine.classic.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.WatermarkType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

public class WatermarkTest {

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testCreation() {
    Watermark watermark = new Watermark();
    assertThat( watermark.getElementType(), is( instanceOf( WatermarkType.class ) ) );

    watermark = new Watermark( true, true );
    assertThat( watermark.isDisplayOnLastPage(), is( equalTo( false ) ) );
    assertThat( watermark.isDisplayOnFirstPage(), is( equalTo( false ) ) );

    watermark = new Watermark( false, false );
    assertThat( watermark.isDisplayOnLastPage(), is( equalTo( false ) ) );
    assertThat( watermark.isDisplayOnFirstPage(), is( equalTo( false ) ) );
  }

  @Test
  public void testGetSubreportCount() {
    Watermark watermark = new Watermark();
    assertThat( watermark.getSubReportCount(), is( equalTo( 0 ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testgetSubReport() {
    Watermark watermark = new Watermark();
    watermark.getSubReport( 0 );
  }

  @Test
  public void testIsSticky() {
    Watermark watermark = new Watermark();
    watermark.getStyle().setBooleanStyleProperty( BandStyleKeys.STICKY, true );
    assertThat( watermark.isSticky(), is( equalTo( true ) ) );
  }

  @Test
  public void testSetSticky() {
    Watermark watermark = new Watermark();
    watermark.setSticky( true );
    boolean result = watermark.getStyle().getBooleanStyleProperty( BandStyleKeys.STICKY );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Test
  public void testGetSubReports() {
    Watermark watermark = new Watermark();
    assertThat( watermark.getSubReports(), is( equalTo( new SubReport[]{ } ) ) );
  }

  @Test
  public void testGetDefaultStyleSheet() {
    Watermark watermark = new Watermark();
    assertThat( watermark.getDefaultStyleSheet(), is( notNullValue() ) );
  }
}
