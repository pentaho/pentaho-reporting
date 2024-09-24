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
import org.pentaho.reporting.engine.classic.core.filter.types.bands.DetailsHeaderType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

public class DetailsHeaderTest {

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testCreationHeader() {
    DetailsHeader header = new DetailsHeader();
    assertThat( header.getElementType(), is( instanceOf( DetailsHeaderType.class ) ) );
    assertThat( header.getStyle().getBooleanStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE ),
        is( equalTo( true ) ) );
  }

  @Test
  public void testGetSubreportCount() {
    DetailsHeader header = new DetailsHeader();
    assertThat( header.getSubReportCount(), is( equalTo( 0 ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testgetSubReport() {
    DetailsHeader header = new DetailsHeader();
    header.getSubReport( 0 );
  }

  @Test
  public void testIsRepeat() {
    DetailsHeader header = new DetailsHeader();
    header.getStyle().setBooleanStyleProperty( BandStyleKeys.REPEAT_HEADER, true );
    assertThat( header.isRepeat(), is( equalTo( true ) ) );
  }

  @Test
  public void testSetRepeat() {
    DetailsHeader header = new DetailsHeader();
    header.setRepeat( true );
    boolean result = header.getStyle().getBooleanStyleProperty( BandStyleKeys.REPEAT_HEADER );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Test
  public void testIsSticky() {
    DetailsHeader header = new DetailsHeader();
    header.getStyle().setBooleanStyleProperty( BandStyleKeys.STICKY, true );
    assertThat( header.isSticky(), is( equalTo( true ) ) );
  }

  @Test
  public void testSetSticky() {
    DetailsHeader header = new DetailsHeader();
    header.setSticky( true );
    boolean result = header.getStyle().getBooleanStyleProperty( BandStyleKeys.STICKY );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Test
  public void testGetSubReports() {
    DetailsHeader header = new DetailsHeader();
    assertThat( header.getSubReports(), is( equalTo( new SubReport[]{ } ) ) );
  }

  @Test
  public void testGetDefaultStyleSheet() {
    DetailsHeader header = new DetailsHeader();
    assertThat( header.getDefaultStyleSheet(), is( notNullValue() ) );
  }
}
