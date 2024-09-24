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

package org.pentaho.reporting.engine.classic.core.sorting;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class GenericComparatorTest {

  @Test
  public void testNullableValues() {
    int result = GenericComparator.INSTANCE.compare( null, null );
    assertThat( result, equalTo( 0 ) );

    result = GenericComparator.INSTANCE.compare( null, 1 );
    assertThat( result, equalTo( -1 ) );

    result = GenericComparator.INSTANCE.compare( 1, null );
    assertThat( result, equalTo( 1 ) );
  }

  @Test
  public void testNumbers() {
    int result = GenericComparator.INSTANCE.compare( 10, 10 );
    assertThat( result, equalTo( 0 ) );

    result = GenericComparator.INSTANCE.compare( "10", 10 );
    assertThat( result, equalTo( 0 ) );
  }

  @Test
  public void testComparableValues() {
    int result = GenericComparator.INSTANCE.compare( "string", "string" );
    assertThat( result, equalTo( 0 ) );

    result = GenericComparator.INSTANCE.compare( new InternalValue( "string" ), "string" );
    assertThat( result, equalTo( 0 ) );
  }

  @Test
  public void testStrings() {
    InternalValue a = new InternalValue( "123" );
    InternalValue b = new InternalValue( "123" );
    int result = GenericComparator.INSTANCE.compare( a, b );
    assertThat( result, equalTo( 0 ) );
  }

  private class InternalValue {
    private String value;

    public InternalValue( String value ) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }
}
