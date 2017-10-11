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

package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.Second;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;
import org.jfree.util.Log;
import org.pentaho.reporting.engine.classic.core.util.StrokeUtility;

import java.awt.*;

/**
 * This class is a monolithic mess that was a left over of the chart-component. Therefore it must not be used anymore. A
 * utility method that collects properties and methods which are used in only one class is a sign of bad design.
 *
 * @deprecated Do not use this class, do not extend this class. This thing will be removed!
 */
public final class ChartConstants {
  private ChartConstants() {
  }

  public static final String DIAL_CHART_STR = "DialChart"; //$NON-NLS-1$

  public static final String PIE_CHART_STR = "PieChart"; //$NON-NLS-1$

  public static final String PIE_GRID_CHART_STR = "PieGrid"; //$NON-NLS-1$

  public static final String BAR_CHART_STR = "BarChart"; //$NON-NLS-1$

  public static final String LINE_CHART_STR = "LineChart"; //$NON-NLS-1$

  public static final String AREA_CHART_STR = "AreaChart"; //$NON-NLS-1$

  public static final String STEP_CHART_STR = "StepChart"; //$NON-NLS-1$

  public static final String STEP_AREA_CHART_STR = "StepAreaChart"; //$NON-NLS-1$

  public static final String DIFFERENCE_CHART_STR = "DifferenceChart"; //$NON-NLS-1$

  public static final String DOT_CHART_STR = "DotChart"; //$NON-NLS-1$

  //new chart type
  public static final String BAR_LINE_CHART_STR = "BarLineChart"; //$NON-NLS-1$

  public static final String BUBBLE_CHART_STR = "BubbleChart"; //$NON-NLS-1$

  // end new chart types

  public static final int UNDEFINED_CHART_TYPE = -1;

  public static final int DIAL_CHART_TYPE = 0;

  public static final int THERMOMETER_CHART_TYPE = 1;

  public static final int PIE_CHART_TYPE = 2;

  public static final int PIE_GRID_CHART_TYPE = 3;

  public static final int BAR_CHART_TYPE = 4;

  public static final int LINE_CHART_TYPE = 5;

  public static final int AREA_CHART_TYPE = 6;

  public static final int STEP_CHART_TYPE = 7;

  public static final int STEP_AREA_CHART_TYPE = 8;

  public static final int DIFFERENCE_CHART_TYPE = 9;

  public static final int DOT_CHART_TYPE = 10;

  //new chart types
  public static final int BAR_LINE_CHART_TYPE = 11;

  public static final int BUBBLE_CHART_TYPE = 12;

  // end new chart type

  public static final String XY_SERIES_COLLECTION_STR = "XYSeriesCollection"; //$NON-NLS-1$

  public static final String XYZ_SERIES_COLLECTION_STR = "XYZSeriesCollection"; //$NON-NLS-1$

  public static final String TIME_SERIES_COLLECTION_STR = "TimeSeriesCollection"; //$NON-NLS-1$

  public static final String CATAGORY_DATASET_STR = "CategoryDataset"; //$NON-NLS-1$

  public static final String DAY_PERIOD_TYPE_STR = "Day"; //$NON-NLS-1$

  public static final String FIXEDMILLISECOND_PERIOD_TYPE_STR = "FixedMillisecond"; //$NON-NLS-1$

  public static final String HOUR_PERIOD_TYPE_STR = "Hour"; //$NON-NLS-1$

  public static final String MILLISECOND_PERIOD_TYPE_STR = "Millisecond"; //$NON-NLS-1$

  public static final String MINUTE_PERIOD_TYPE_STR = "Minute"; //$NON-NLS-1$

  public static final String MONTH_PERIOD_TYPE_STR = "Month"; //$NON-NLS-1$

  public static final String QUARTER_PERIOD_TYPE_STR = "Quarter"; //$NON-NLS-1$

  public static final String SECOND_PERIOD_TYPE_STR = "Second"; //$NON-NLS-1$

  public static final String WEEK_PERIOD_TYPE_STR = "Week"; //$NON-NLS-1$

  public static final String YEAR_PERIOD_TYPE_STR = "Year"; //$NON-NLS-1$

  public static final String VERTICAL_ORIENTATION = "Vertical"; //$NON-NLS-1$

  public static final String HORIZONTAL_ORIENTATION = "Horizontal"; //$NON-NLS-1$

  public static final String LINE_STYLE_SOLID_STR = "solid"; //$NON-NLS-1$

  public static final String LINE_STYLE_DASH_STR = "dash"; //$NON-NLS-1$

  public static final String LINE_STYLE_DOT_STR = "dot"; //$NON-NLS-1$

  public static final String LINE_STYLE_DASHDOT_STR = "dashdot"; //$NON-NLS-1$

  public static final String LINE_STYLE_DASHDOTDOT_STR = "dashdotdot"; //$NON-NLS-1$


  /**
   * @param type int type for chart
   * @return String representing the chart
   */
  public static String getChartTypeName( final int type ) {

    String rtn = ""; //$NON-NLS-1$

    switch( type ) {
      case PIE_CHART_TYPE:
        rtn = PIE_CHART_STR;
        break;
      case PIE_GRID_CHART_TYPE:
        rtn = PIE_GRID_CHART_STR;
        break;
      case BAR_CHART_TYPE:
        rtn = BAR_CHART_STR;
        break;
      case LINE_CHART_TYPE:
        rtn = LINE_CHART_STR;
        break;
      case BAR_LINE_CHART_TYPE:
        rtn = BAR_LINE_CHART_STR;
        break;
      case BUBBLE_CHART_TYPE:
        rtn = BUBBLE_CHART_STR;
        break;
      case DIAL_CHART_TYPE:
        rtn = DIAL_CHART_STR;
        break;
      case DIFFERENCE_CHART_TYPE:
        rtn = DIFFERENCE_CHART_STR;
        break;
      case DOT_CHART_TYPE:
        rtn = DOT_CHART_STR;
        break;
      case STEP_AREA_CHART_TYPE:
        rtn = STEP_AREA_CHART_STR;
        break;
      case STEP_CHART_TYPE:
        rtn = STEP_CHART_STR;
        break;
      case AREA_CHART_TYPE:
        rtn = AREA_CHART_STR;
        break;
      default:
    }

    return rtn;
  }

  public static Class getTimePeriodClass( final String timePeriodStr ) {
    Class retClass = Millisecond.class;
    if ( timePeriodStr.equalsIgnoreCase( SECOND_PERIOD_TYPE_STR ) ) {
      retClass = Second.class;
    } else if ( timePeriodStr.equalsIgnoreCase( MINUTE_PERIOD_TYPE_STR ) ) {
      retClass = Minute.class;
    } else if ( timePeriodStr.equalsIgnoreCase( HOUR_PERIOD_TYPE_STR ) ) {
      retClass = Hour.class;
    } else if ( timePeriodStr.equalsIgnoreCase( DAY_PERIOD_TYPE_STR ) ) {
      retClass = Day.class;
    } else if ( timePeriodStr.equalsIgnoreCase( WEEK_PERIOD_TYPE_STR ) ) {
      retClass = Week.class;
    } else if ( timePeriodStr.equalsIgnoreCase( MONTH_PERIOD_TYPE_STR ) ) {
      retClass = Month.class;
    } else if ( timePeriodStr.equalsIgnoreCase( QUARTER_PERIOD_TYPE_STR ) ) {
      retClass = Quarter.class;
    } else if ( timePeriodStr.equalsIgnoreCase( YEAR_PERIOD_TYPE_STR ) ) {
      retClass = Year.class;
    }
    return retClass;
  }

  /**
   * @return java.awt.Stroke for JFreeChart renderer to draw lines
   */
  public static Stroke translateLineStyle( float lineWidth, final String lineStyle ) {
    // Negative linewidths not allowed, reset to default.
    if ( lineWidth < 0 ) {
      Log.error( ( "LineChartExpression.ERROR_0001_INVALID_LINE_WIDTH" ) ); //$NON-NLS-1$
      lineWidth = 1.0f;
    }

    final int strokeType;
    if ( LINE_STYLE_DASH_STR.equals( lineStyle ) ) {
      strokeType = StrokeUtility.STROKE_DASHED;
    } else if ( LINE_STYLE_DOT_STR.equals( lineStyle ) ) {
      strokeType = StrokeUtility.STROKE_DOTTED;
    } else if ( LINE_STYLE_DASHDOT_STR.equals( lineStyle ) ) {
      strokeType = StrokeUtility.STROKE_DOT_DASH;
    } else if ( LINE_STYLE_DASHDOTDOT_STR.equals( lineStyle ) ) {
      strokeType = StrokeUtility.STROKE_DOT_DOT_DASH;
    } else {
      if ( lineWidth == 0 ) {
        strokeType = StrokeUtility.STROKE_NONE;
      } else {
        strokeType = StrokeUtility.STROKE_SOLID;
      }
    }

    return StrokeUtility.createStroke( strokeType, lineWidth );
  }

}
