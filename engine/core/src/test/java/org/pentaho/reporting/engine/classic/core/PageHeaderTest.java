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
