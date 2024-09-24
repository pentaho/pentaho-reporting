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
