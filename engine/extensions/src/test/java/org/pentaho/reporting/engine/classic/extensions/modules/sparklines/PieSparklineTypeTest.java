/*!
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
 * Copyright (c) 2005-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.libsparklines.PieGraphDrawable;

public class PieSparklineTypeTest {

  private PieSparklineType pieType = new PieSparklineType();

  @Test
  public void testGetValue() {
    ExpressionRuntime runtime = mock( ExpressionRuntime.class );
    ReportElement element = mock( ReportElement.class );

    Object result = pieType.getValue( runtime, element );
    assertThat( result, is( nullValue() ) );

    doReturn( 1.0 ).when( element ).getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
    doReturn( 1 ).when( element ).getAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.START_ANGLE );
    doReturn( 2 ).when( element ).getAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.LOW_SLICE );
    doReturn( 2.5 ).when( element ).getAttribute( SparklineAttributeNames.NAMESPACE,
        SparklineAttributeNames.MEDIUM_SLICE );
    doReturn( 5.6 ).when( element )
        .getAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.HIGH_SLICE );
    doReturn( true ).when( element ).getAttribute( SparklineAttributeNames.NAMESPACE,
        SparklineAttributeNames.COUNTER_CLOCKWISE );
    result = pieType.getValue( runtime, element );
    assertThat( result, is( instanceOf( PieSparklinesWrapper.class ) ) );
    PieSparklinesWrapper wrap = (PieSparklinesWrapper) result;
    assertThat( wrap.getBackend(), is( instanceOf( PieGraphDrawable.class ) ) );
    PieGraphDrawable dr = (PieGraphDrawable) wrap.getBackend();
    assertThat( dr.getHighSlice(), is( equalTo( (Number) 5.6 ) ) );
    assertThat( dr.getLowSlice(), is( equalTo( (Number) 2 ) ) );
    assertThat( dr.getMediumSlice(), is( equalTo( (Number) 2.5 ) ) );
    assertThat( dr.getStartAngle(), is( equalTo( 1 ) ) );
    assertThat( dr.isCounterClockWise(), is( equalTo( true ) ) );
  }

  @Test
  public void testGetDesignValue() {
    ExpressionRuntime runtime = mock( ExpressionRuntime.class );
    ReportElement element = mock( ReportElement.class );

    Object result = pieType.getDesignValue( runtime, element );
    assertThat( result, is( instanceOf( PieSparklinesWrapper.class ) ) );
    PieSparklinesWrapper wrap = (PieSparklinesWrapper) result;
    assertThat( wrap.getBackend(), is( instanceOf( PieGraphDrawable.class ) ) );
    PieGraphDrawable dr = (PieGraphDrawable) wrap.getBackend();
    assertThat( dr.getHighSlice(), is( equalTo( (Number) 1.0 ) ) );
    assertThat( dr.getLowSlice(), is( equalTo( (Number) 0.3 ) ) );
    assertThat( dr.getMediumSlice(), is( equalTo( (Number) 0.7 ) ) );
    assertThat( dr.getStartAngle(), is( equalTo( 0 ) ) );
    assertThat( dr.isCounterClockWise(), is( equalTo( false ) ) );
  }
}
