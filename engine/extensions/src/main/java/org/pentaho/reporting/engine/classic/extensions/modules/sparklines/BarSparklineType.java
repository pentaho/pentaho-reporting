/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
