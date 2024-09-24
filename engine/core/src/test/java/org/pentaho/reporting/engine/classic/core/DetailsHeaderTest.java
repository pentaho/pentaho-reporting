/*
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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

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
