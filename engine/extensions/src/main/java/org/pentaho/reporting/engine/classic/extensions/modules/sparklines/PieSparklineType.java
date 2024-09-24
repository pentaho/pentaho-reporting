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
import org.pentaho.reporting.libraries.libsparklines.PieGraphDrawable;

/**
 * This class is the Element type implementation of Sparkline pie graph.<br/>
 * This Element type allows the configuration of slices color, slices repartition, clockwise (or counter) drawing and
 * the starting angle in degrees (0 the default is 12 o'clock).
 *
 * @author Cedric Pronzato
 */
public class PieSparklineType extends ContentType {
  public static final PieSparklineType INSTANCE = new PieSparklineType();

  public PieSparklineType() {
    super( "pie-sparkline" );
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
    final Object retval = ElementTypeUtils.queryFieldOrValue( runtime, element );
    if ( retval instanceof Number == false ) {
      final Object nullValue = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE );
      return filter( runtime, element, nullValue );
    }
    final Number numbers = (Number) retval;

    final int startAngle =
        ElementTypeUtils.getIntAttribute( element, SparklineAttributeNames.NAMESPACE,
            SparklineAttributeNames.START_ANGLE, 0 );
    final Number lowSlice =
        ElementTypeUtils.getNumberAttribute( element, SparklineAttributeNames.NAMESPACE,
            SparklineAttributeNames.LOW_SLICE, new Double( 0.30 ) );
    final Number mediumSlice =
        ElementTypeUtils.getNumberAttribute( element, SparklineAttributeNames.NAMESPACE,
            SparklineAttributeNames.MEDIUM_SLICE, new Double( 0.70 ) );
    final Number highSlice =
        ElementTypeUtils.getNumberAttribute( element, SparklineAttributeNames.NAMESPACE,
            SparklineAttributeNames.HIGH_SLICE, new Double( 1 ) );
    final boolean clockwise =
        ElementTypeUtils.getBooleanAttribute( element, SparklineAttributeNames.NAMESPACE,
            SparklineAttributeNames.COUNTER_CLOCKWISE, false );

    final PieGraphDrawable drawable = new PieGraphDrawable();
    drawable.setValue( numbers );
    drawable.setStartAngle( startAngle );
    drawable.setLowSlice( lowSlice );
    drawable.setMediumSlice( mediumSlice );
    drawable.setHighSlice( highSlice );
    drawable.setCounterClockWise( clockwise );
    return new PieSparklinesWrapper( drawable );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    Object retval = ElementTypeUtils.queryStaticValue( element );
    if ( retval instanceof Number == false ) {
      retval = new Double( 0.75 );
    }
    final Number numbers = (Number) retval;
    final int startAngle =
        ElementTypeUtils.getIntAttribute( element, SparklineAttributeNames.NAMESPACE,
            SparklineAttributeNames.START_ANGLE, 0 );
    final Number lowSlice =
        ElementTypeUtils.getNumberAttribute( element, SparklineAttributeNames.NAMESPACE,
            SparklineAttributeNames.LOW_SLICE, new Double( 0.30 ) );
    final Number mediumSlice =
        ElementTypeUtils.getNumberAttribute( element, SparklineAttributeNames.NAMESPACE,
            SparklineAttributeNames.MEDIUM_SLICE, new Double( 0.70 ) );
    final Number highSlice =
        ElementTypeUtils.getNumberAttribute( element, SparklineAttributeNames.NAMESPACE,
            SparklineAttributeNames.HIGH_SLICE, new Double( 1 ) );
    final boolean clockwise =
        ElementTypeUtils.getBooleanAttribute( element, SparklineAttributeNames.NAMESPACE,
            SparklineAttributeNames.COUNTER_CLOCKWISE, false );

    final PieGraphDrawable drawable = new PieGraphDrawable();
    drawable.setValue( numbers );
    drawable.setStartAngle( startAngle );
    drawable.setLowSlice( lowSlice );
    drawable.setMediumSlice( mediumSlice );
    drawable.setHighSlice( highSlice );
    drawable.setCounterClockWise( clockwise );
    return new PieSparklinesWrapper( drawable );
  }
}
