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
import org.pentaho.reporting.engine.classic.core.filter.types.bands.DetailsFooterType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

public class DetailsFooterTest {

  @BeforeClass
  public static void init() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testCreation() {
    DetailsFooter footer = new DetailsFooter();
    assertThat( footer.getElementType(), is( instanceOf( DetailsFooterType.class ) ) );
    assertThat( footer.getStyle().getBooleanStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE ),
        is( equalTo( true ) ) );
  }

  @Test
  public void testGetSubreportCount() {
    DetailsFooter footer = new DetailsFooter();
    assertThat( footer.getSubReportCount(), is( equalTo( 0 ) ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetSubReport() {
    DetailsFooter footer = new DetailsFooter();
    footer.getSubReport( 0 );
  }

  @Test
  public void testIsRepeat() {
    DetailsFooter footer = new DetailsFooter();
    footer.getStyle().setBooleanStyleProperty( BandStyleKeys.REPEAT_HEADER, true );
    assertThat( footer.isRepeat(), is( equalTo( true ) ) );
  }

  @Test
  public void testSetRepeat() {
    DetailsFooter footer = new DetailsFooter();
    footer.setRepeat( true );
    boolean result = footer.getStyle().getBooleanStyleProperty( BandStyleKeys.REPEAT_HEADER );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Test
  public void testIsSticky() {
    DetailsFooter footer = new DetailsFooter();
    footer.getStyle().setBooleanStyleProperty( BandStyleKeys.STICKY, true );
    assertThat( footer.isSticky(), is( equalTo( true ) ) );
  }

  @Test
  public void testSetSticky() {
    DetailsFooter footer = new DetailsFooter();
    footer.setSticky( true );
    boolean result = footer.getStyle().getBooleanStyleProperty( BandStyleKeys.STICKY );
    assertThat( result, is( equalTo( true ) ) );
  }

  @Test
  public void testGetSubReports() {
    DetailsFooter footer = new DetailsFooter();
    assertThat( footer.getSubReports(), is( equalTo( new SubReport[]{ } ) ) );
  }

  @Test
  public void testGetDefaultStyleSheet() {
    DetailsFooter footer = new DetailsFooter();
    assertThat( footer.getDefaultStyleSheet(), is( notNullValue() ) );
  }
}
