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
import org.pentaho.reporting.libraries.libsparklines.BarGraphDrawable;

public class BarSparklineTypeTest {

  private BarSparklineType barType = new BarSparklineType();

  @Test
  public void testGetValue() {
    ExpressionRuntime runtime = mock( ExpressionRuntime.class );
    ReportElement element = mock( ReportElement.class );

    Object result = barType.getValue( runtime, element );
    assertThat( result, is( nullValue() ) );

    Number[] data = new Number[] { 1.0 };
    doReturn( data ).when( element ).getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
    doReturn( 5 ).when( element ).getAttribute( SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.SPACING );
    result = barType.getValue( runtime, element );
    assertThat( result, is( instanceOf( BarSparklinesWrapper.class ) ) );
    BarSparklinesWrapper wrap = (BarSparklinesWrapper) result;
    assertThat( wrap.getBackend(), is( instanceOf( BarGraphDrawable.class ) ) );
    BarGraphDrawable dr = (BarGraphDrawable) wrap.getBackend();
    assertThat( dr.getData(), is( equalTo( data ) ) );
    assertThat( dr.getSpacing(), is( equalTo( 5 ) ) );
  }

  @Test
  public void testGetDesignValue() {
    ExpressionRuntime runtime = mock( ExpressionRuntime.class );
    ReportElement element = mock( ReportElement.class );

    Object result = barType.getDesignValue( runtime, element );
    assertThat( result, is( instanceOf( BarSparklinesWrapper.class ) ) );
    BarSparklinesWrapper wrap = (BarSparklinesWrapper) result;
    assertThat( wrap.getBackend(), is( instanceOf( BarGraphDrawable.class ) ) );
    BarGraphDrawable dr = (BarGraphDrawable) wrap.getBackend();
    assertThat( dr.getData(), is( equalTo( new Number[] { 10, 5, 6, 3, 1, 2, 7, 9 } ) ) );
    assertThat( dr.getSpacing(), is( equalTo( 2 ) ) );
  }
}
