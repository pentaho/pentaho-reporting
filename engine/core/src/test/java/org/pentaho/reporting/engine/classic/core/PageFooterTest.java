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


package org.pentaho.reporting.engine.classic.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.PageFooterType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

public class PageFooterTest {

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testCreation() {
    PageFooter footer = new PageFooter();
    assertThat( footer.getElementType(), is( instanceOf( PageFooterType.class ) ) );

    footer = new PageFooter( true, true );
    assertThat( footer.isDisplayOnLastPage(), is( equalTo( true ) ) );
    assertThat( footer.isDisplayOnFirstPage(), is( equalTo( true ) ) );
  }

  @Test
  public void testGetSubreportCount() {
    PageFooter footer = new PageFooter();
    assertThat( footer.getSubReportCount(), is( equalTo( 0 ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testgetSubReport() {
    PageFooter footer = new PageFooter();
    footer.getSubReport( 0 );
  }

  @Test
  public void testIsSticky() {
    PageFooter footer = new PageFooter();
    footer.getStyle().setBooleanStyleProperty( BandStyleKeys.STICKY, true );
    assertThat( footer.isSticky(), is( equalTo( true ) ) );
  }

  @Test
  public void testSetSticky() {
    PageFooter footer = new PageFooter();
    footer.setSticky( true );
    boolean result = footer.getStyle().getBooleanStyleProperty( BandStyleKeys.STICKY );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Test
  public void testGetSubReports() {
    PageFooter footer = new PageFooter();
    assertThat( footer.getSubReports(), is( equalTo( new SubReport[]{ } ) ) );
  }

  @Test
  public void testGetDefaultStyleSheet() {
    PageFooter footer = new PageFooter();
    assertThat( footer.getDefaultStyleSheet(), is( notNullValue() ) );
  }
}
