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
import org.pentaho.reporting.engine.classic.core.filter.types.bands.PageHeaderType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

public class PageHeaderTest {

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testCreationHeader() {
    PageHeader header = new PageHeader();
    assertThat( header.getElementType(), is( instanceOf( PageHeaderType.class ) ) );

    header = new PageHeader( true, true );
    assertThat( header.isDisplayOnLastPage(), is( equalTo( true ) ) );
    assertThat( header.isDisplayOnFirstPage(), is( equalTo( true ) ) );
  }

  @Test
  public void testGetSubreportCount() {
    PageHeader header = new PageHeader();
    assertThat( header.getSubReportCount(), is( equalTo( 0 ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetSubReport() {
    PageHeader header = new PageHeader();
    header.getSubReport( 0 );
  }

  @Test
  public void testIsSticky() {
    PageHeader header = new PageHeader();
    header.getStyle().setBooleanStyleProperty( BandStyleKeys.STICKY, true );
    assertThat( header.isSticky(), is( equalTo( true ) ) );
  }

  @Test
  public void testSetSticky() {
    PageHeader header = new PageHeader();
    header.setSticky( true );
    boolean result = header.getStyle().getBooleanStyleProperty( BandStyleKeys.STICKY );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Test
  public void testGetSubReports() {
    PageHeader header = new PageHeader();
    assertThat( header.getSubReports(), is( equalTo( new SubReport[]{ } ) ) );
  }

  @Test
  public void testGetDefaultStyleSheet() {
    PageHeader header = new PageHeader();
    assertThat( header.getDefaultStyleSheet(), is( notNullValue() ) );
  }
}
