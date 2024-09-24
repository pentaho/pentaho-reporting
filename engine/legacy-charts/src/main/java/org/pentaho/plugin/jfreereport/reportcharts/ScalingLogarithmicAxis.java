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

package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ScalingLogarithmicAxis extends LogarithmicAxis {

  private static final long serialVersionUID = 1L;
  private int ticksVertical = -1;

  public ScalingLogarithmicAxis( final String label ) {
    super( label );
  }

  public void autoAdjustRange() {
    super.autoAdjustRange();
    if ( getPlot() instanceof ValueAxisPlot ) {
      final Range range = this.getRange();
      setRange( new Range( range.getLowerBound() * 10.0, range.getUpperBound() * 10.0 ), false, false );
      setupSmallLogFlag();
    }
  }

  protected List refreshTicksVertical( final Graphics2D g2,
                                       final Rectangle2D dataArea,
                                       final RectangleEdge edge ) {

    final ArrayList ticks = new ArrayList();

    // get lower bound value:
    double lowerBoundVal = getRange().getLowerBound();
    // if small log values and lower bound value too small
    // then set to a small value (don't allow <= 0):
    if ( this.smallLogFlag && lowerBoundVal < SMALL_LOG_VALUE ) {
      lowerBoundVal = SMALL_LOG_VALUE;
    }
    // get upper bound value
    final double upperBoundVal = getRange().getUpperBound();

    // get log10 version of lower bound and round to integer:
    int iBegCount = (int) Math.rint( switchedLog10( lowerBoundVal ) );
    // get log10 version of upper bound and round to integer:
    final int iEndCount = (int) Math.rint( switchedLog10( upperBoundVal ) );

    if ( iBegCount == iEndCount && iBegCount > 0
      && Math.pow( 10, iBegCount ) > lowerBoundVal ) {
      // only 1 power of 10 value, it's > 0 and its resulting
      // tick value will be larger than lower bound of data
      --iBegCount;       // decrement to generate more ticks
    }

    double tickVal;
    String tickLabel;
    boolean zeroTickFlag = false;
    final int[] intervals = { 10, 10, 10, 10, 10, 5, 5, 5, 3, 3 };

    for ( int i = iBegCount; i <= iEndCount; i++ ) {
      // for each tick with a label to be displayed
      int jEndCount = getTicksVertical();
      if ( jEndCount == -1 ) {
        jEndCount = Math.abs( iEndCount - iBegCount ) < 10 ? intervals[ iEndCount - iBegCount ] : 1;
      }

      if ( i == iEndCount ) {
        jEndCount = 1;
      }

      for ( int j = 0; j < jEndCount; j++ ) {
        // for each tick to be displayed
        if ( this.smallLogFlag ) {
          // small log values in use
          tickVal = Math.pow( 10, i ) + ( Math.pow( 10, i ) * j );
          if ( j == 0 ) {
            // first tick of group; create label text
            if ( this.log10TickLabelsFlag ) {
              // if flag then
              tickLabel = "10^" + i;   // create "log10"-type label
            } else {    // not "log10"-type label
              if ( this.expTickLabelsFlag ) {
                // if flag then
                tickLabel = "1e" + i;  // create "1e#"-type label
              } else {    // not "1e#"-type label
                if ( i >= 0 ) {   // if positive exponent then
                  // make integer
                  final NumberFormat format
                    = getNumberFormatOverride();
                  if ( format != null ) {
                    tickLabel = format.format( tickVal );
                  } else {
                    tickLabel = LogCategoryItemLabelGenerator.formatValue( new Double( tickVal ) );
                    /*tickLabel = Long.toString((long) Math.rint(tickVal));*/
                  }
                } else {
                  // negative exponent; create fractional value
                  // set exact number of fractional digits to
                  // be shown:
                  this.numberFormatterObj
                    .setMaximumFractionDigits( -i );
                  // create tick label:
                  tickLabel = this.numberFormatterObj.format(
                    tickVal
                  );
                }
              }
            }
          } else {   // not first tick to be displayed
            tickLabel = "";     // no tick label
          }
        } else { // not small log values in use; allow for values <= 0
          if ( zeroTickFlag ) {      // if did zero tick last iter then
            --j;
          }               // decrement to do 1.0 tick now
          tickVal = ( i >= 0 ) ? Math.pow( 10, i ) + ( Math.pow( 10, i ) * j )
            : j == 0 ? -Math.pow( 10, -i ) : -( Math.pow( 10, -i ) - ( Math.pow( 10, -i - 1 ) * ( 9 - j ) ) );
          if ( j == 0 ) {  // first tick of group
            if ( !zeroTickFlag ) {     // did not do zero tick last
              // iteration
              if ( i > iBegCount && i < iEndCount
                && Math.abs( tickVal - 1.0 ) < 0.0001 ) {
                // not first or last tick on graph and value
                // is 1.0
                tickVal = 0.0;        // change value to 0.0
                zeroTickFlag = true;  // indicate zero tick
                tickLabel = "0";      // create label for tick
              } else {
                // first or last tick on graph or value is 1.0
                // create label for tick:
                if ( this.log10TickLabelsFlag ) {
                  // create "log10"-type label
                  tickLabel = ( ( ( i < 0 ) ? "-" : "" )
                    + "10^" + Math.abs( i ) );
                } else {
                  if ( this.expTickLabelsFlag ) {
                    // create "1e#"-type label
                    tickLabel = ( ( ( i < 0 ) ? "-" : "" )
                      + "1e" + Math.abs( i ) );
                  } else {
                    final NumberFormat format
                      = getNumberFormatOverride();
                    if ( format != null ) {
                      tickLabel = format.format( tickVal );
                    } else {
                      tickLabel = LogCategoryItemLabelGenerator.formatValue( new Double( tickVal ) );/*
                      tickLabel =  Long.toString((long) Math.rint(tickVal));*/
                    }
                  }
                }
              }
            } else {     // did zero tick last iteration
              tickLabel = "";         // no label
              zeroTickFlag = false;   // clear flag
            }
          } else {       // not first tick of group
            tickLabel = "";           // no label
            zeroTickFlag = false;     // make sure flag cleared
          }
        }

        if ( tickVal > upperBoundVal ) {
          return ticks;  // if past highest data value then exit method
        }

        if ( tickVal >= lowerBoundVal - SMALL_LOG_VALUE ) {
          // tick value not below lowest data value
          final TextAnchor anchor;
          final TextAnchor rotationAnchor;
          double angle = 0.0;
          if ( isVerticalTickLabels() ) {
            if ( edge == RectangleEdge.LEFT ) {
              anchor = TextAnchor.BOTTOM_CENTER;
              rotationAnchor = TextAnchor.BOTTOM_CENTER;
              angle = -Math.PI / 2.0;
            } else {
              anchor = TextAnchor.BOTTOM_CENTER;
              rotationAnchor = TextAnchor.BOTTOM_CENTER;
              angle = Math.PI / 2.0;
            }
          } else {
            if ( edge == RectangleEdge.LEFT ) {
              anchor = TextAnchor.CENTER_RIGHT;
              rotationAnchor = TextAnchor.CENTER_RIGHT;
            } else {
              anchor = TextAnchor.CENTER_LEFT;
              rotationAnchor = TextAnchor.CENTER_LEFT;
            }
          }

          ticks.add( new NumberTick( new Double( tickVal ), tickLabel, anchor, rotationAnchor, angle ) );
        }
      }
    }
    return ticks;
  }

  public void setTicksVertical( final int ticksVertical ) {
    this.ticksVertical = ticksVertical;
  }

  public int getTicksVertical() {
    return ticksVertical;
  }

}
