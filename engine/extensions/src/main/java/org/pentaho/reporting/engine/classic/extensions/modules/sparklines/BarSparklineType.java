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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.filter.types.ElementTypeUtils;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.libsparklines.BarGraphDrawable;

import java.util.Locale;

/**
 * This class is the Element type implementation of Sparkline bar graph.<br/>
 * This Element type allows the configuration of the default bars color, the bar color above average data points, the
 * color of the last bar and the spacing between data points.
 *
 * @author Thomas Morgner
 */
public class BarSparklineType extends ContentType {
  public static final BarSparklineType INSTANCE = new BarSparklineType();

  public BarSparklineType() {
    super( "bar-sparkline" );
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   *          the element for which the data is computed.
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Object value = ElementTypeUtils.queryFieldOrValue( runtime, element );
    final Number[] numbers = ElementTypeUtils.getData( value );
    if ( numbers == null ) {
      final Object nullValue = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE );
      return filter( runtime, element, nullValue );
    }

    final int spacing =
        ElementTypeUtils.getIntAttribute( element, SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.SPACING,
            2 );

    final BarGraphDrawable drawable = new BarGraphDrawable();
    drawable.setData( numbers );
    drawable.setSpacing( spacing );
    return new BarSparklinesWrapper( drawable );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Object value = ElementTypeUtils.queryStaticValue( element );
    Number[] numbers = ElementTypeUtils.getData( value );
    if ( numbers == null ) {
      numbers =
          new Number[] { new Integer( 10 ), new Integer( 5 ), new Integer( 6 ), new Integer( 3 ), new Integer( 1 ),
            new Integer( 2 ), new Integer( 7 ), new Integer( 9 ) };
    }

    final int spacing =
        ElementTypeUtils.getIntAttribute( element, SparklineAttributeNames.NAMESPACE, SparklineAttributeNames.SPACING,
            2 );

    final BarGraphDrawable drawable = new BarGraphDrawable();
    drawable.setData( numbers );
    drawable.setSpacing( spacing );
    return new BarSparklinesWrapper( drawable );
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {

  }
}
