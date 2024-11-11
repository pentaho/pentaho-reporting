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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabHeaderType;

public class CrosstabHeaderTest {

  private CrosstabHeader header;

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Before
  public void setUp() {
    header = new CrosstabHeader();
  }

  @Test
  public void testCreationHeader() {
    assertThat( header.getElementType(), is( instanceOf( CrosstabHeaderType.class ) ) );
  }

  @Test
  public void testGetSubreportCount() {
    assertThat( header.getSubReportCount(), is( equalTo( 0 ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetSubReport() {
    header.getSubReport( 0 );
  }

  @Test
  public void testGetSubReports() {
    assertThat( header.getSubReports(), is( equalTo( new SubReport[] {} ) ) );
  }

  @Test
  public void testGetDefaultStyleSheet() {
    assertThat( header.getDefaultStyleSheet(), is( notNullValue() ) );
  }
}
