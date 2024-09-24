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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.survey;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.filter.types.ElementTypeUtils;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

public class SurveyScaleType extends ContentType {
  public static final SurveyScaleType INSTANCE = new SurveyScaleType();

  public SurveyScaleType() {
    super( "survey-scale" );
  }

  public Object getDesignValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Object value = ElementTypeUtils.queryFieldOrValue( runtime, element );
    Number[] numbers = ElementTypeUtils.getData( value );
    if ( numbers == null ) {
      numbers = new Number[] { Integer.valueOf( 1 ), Integer.valueOf( 2 ), Integer.valueOf( 4 ) };
    }

    final int lowest = ElementTypeUtils.getIntAttribute( element, SurveyModule.NAMESPACE, SurveyModule.LOWEST, 1 );
    final int highest = ElementTypeUtils.getIntAttribute( element, SurveyModule.NAMESPACE, SurveyModule.HIGHEST, 5 );

    final Number rangeLowerBound =
        ElementTypeUtils.getNumberAttribute( element, SurveyModule.NAMESPACE, SurveyModule.RANGE_LOWER_BOUND, null );
    final Number rangeUpperBound =
        ElementTypeUtils.getNumberAttribute( element, SurveyModule.NAMESPACE, SurveyModule.RANGE_UPPER_BOUND, null );

    final SurveyScale drawable = new SurveyScale( lowest, highest, numbers );
    drawable.setRangeLowerBound( rangeLowerBound );
    drawable.setRangeUpperBound( rangeUpperBound );
    return ( drawable );
  }

  public void configureDesignTimeDefaults( final ReportElement element, final Locale locale ) {
    element.setAttribute( SurveyModule.NAMESPACE, SurveyModule.LOWEST, Integer.valueOf( 1 ) );
    element.setAttribute( SurveyModule.NAMESPACE, SurveyModule.HIGHEST, Integer.valueOf( 5 ) );
  }

  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final Object value = ElementTypeUtils.queryFieldOrValue( runtime, element );
    final Number[] numbers = ElementTypeUtils.getData( value );
    if ( numbers == null ) {
      final Object nullValue = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE );
      return filter( runtime, element, nullValue );
    }

    final int lowest = ElementTypeUtils.getIntAttribute( element, SurveyModule.NAMESPACE, SurveyModule.LOWEST, 1 );
    final int highest = ElementTypeUtils.getIntAttribute( element, SurveyModule.NAMESPACE, SurveyModule.HIGHEST, 5 );

    final Number rangeLowerBound =
        ElementTypeUtils.getNumberAttribute( element, SurveyModule.NAMESPACE, SurveyModule.RANGE_LOWER_BOUND, null );
    final Number rangeUpperBound =
        ElementTypeUtils.getNumberAttribute( element, SurveyModule.NAMESPACE, SurveyModule.RANGE_UPPER_BOUND, null );

    final SurveyScale drawable = new SurveyScale( lowest, highest, numbers );
    drawable.setRangeLowerBound( rangeLowerBound );
    drawable.setRangeUpperBound( rangeUpperBound );
    // handles scale-value-font, scale-value-color, fill-paint
    drawable.setAutoConfigure( true );

    final Object tickMarkPaint = element.getAttribute( SurveyModule.NAMESPACE, SurveyModule.TICK_MARK_PAINT );
    if ( tickMarkPaint instanceof Color ) {
      drawable.setRangePaint( (Color) tickMarkPaint );
    }

    final Number lowerMargin =
        ElementTypeUtils.getNumberAttribute( element, SurveyModule.NAMESPACE, SurveyModule.LOWER_MARGIN, null );
    if ( lowerMargin != null ) {
      drawable.setLowerMargin( lowerMargin.doubleValue() );
    }
    final Number upperMargin =
        ElementTypeUtils.getNumberAttribute( element, SurveyModule.NAMESPACE, SurveyModule.UPPER_MARGIN, null );
    if ( upperMargin != null ) {
      drawable.setUpperMargin( upperMargin.doubleValue() );
    }
    final Object defaultShape = element.getAttribute( SurveyModule.NAMESPACE, SurveyModule.DEFAULT_SHAPE );
    if ( defaultShape instanceof SurveyScaleShapeType ) {
      drawable.setDefaultShape( (SurveyScaleShapeType) defaultShape );
    }
    final Object outlineStroke = element.getAttribute( SurveyModule.NAMESPACE, SurveyModule.OUTLINE_STROKE );
    if ( outlineStroke instanceof BasicStroke ) {
      drawable.setOutlineStroke( (BasicStroke) outlineStroke );
    }

    return ( drawable );
  }
}
