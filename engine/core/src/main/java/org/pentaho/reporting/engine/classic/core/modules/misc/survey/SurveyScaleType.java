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
