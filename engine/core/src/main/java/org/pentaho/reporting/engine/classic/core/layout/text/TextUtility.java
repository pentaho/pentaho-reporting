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


package org.pentaho.reporting.engine.classic.core.layout.text;

import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

/**
 * Creation-Date: 04.04.2007, 14:46:14
 *
 * @author Thomas Morgner
 */
public class TextUtility {
  private TextUtility() {
  }

  private static int translateBaselines( final int baseline ) {
    switch ( baseline ) {
      case BaselineInfo.HANGING:
        return ExtendedBaselineInfo.HANGING;
      case BaselineInfo.ALPHABETIC:
        return ExtendedBaselineInfo.ALPHABETHIC;
      case BaselineInfo.CENTRAL:
        return ExtendedBaselineInfo.CENTRAL;
      case BaselineInfo.IDEOGRAPHIC:
        return ExtendedBaselineInfo.IDEOGRAPHIC;
      case BaselineInfo.MATHEMATICAL:
        return ExtendedBaselineInfo.MATHEMATICAL;
      case BaselineInfo.MIDDLE:
        return ExtendedBaselineInfo.MIDDLE;
      default:
        throw new IllegalArgumentException( "Invalid baseline" );
    }
  }

  public static ExtendedBaselineInfo createBaselineInfo( final int codepoint, final FontMetrics fontMetrics,
      final BaselineInfo reusableBaselineInfo ) {
    if ( fontMetrics == null ) {
      throw new NullPointerException( "FontMetrics cannot be null" );
    }

    final BaselineInfo baselineInfo = fontMetrics.getBaselines( codepoint, reusableBaselineInfo );
    final int dominantBaseline = TextUtility.translateBaselines( baselineInfo.getDominantBaseline() );
    final long underlinePosition = fontMetrics.getUnderlinePosition();
    final long strikeThroughPosition = fontMetrics.getStrikeThroughPosition();
    return new DefaultExtendedBaselineInfo( dominantBaseline, baselineInfo, 0, 0, fontMetrics.getMaxHeight(),
        fontMetrics.getMaxHeight(), underlinePosition, strikeThroughPosition );
  }

  /*
   * This method is a workaround for broken font metrics supplied by some truetype fonts.
   * Those fonts trip up Excel's row heights in the workbooks.
   * We therefore try to detect those fonts by checking whether their declared sizes are
   * awfully close to the requested font size and apply some extra padding in that case.
   * For all details please read PRD-5435.
   */
  public static ExtendedBaselineInfo createPaddedBaselineInfo( final int codepoint, final FontMetrics fontMetrics,
                                                         final BaselineInfo reusableBaselineInfo ) {
    if ( fontMetrics == null ) {
      throw new NullPointerException( "FontMetrics cannot be null" );
    }

    final BaselineInfo baselineInfo = fontMetrics.getBaselines( codepoint, reusableBaselineInfo );
    final int dominantBaseline = TextUtility.translateBaselines( baselineInfo.getDominantBaseline() );
    final long underlinePosition = fontMetrics.getUnderlinePosition();
    final long strikeThroughPosition = fontMetrics.getStrikeThroughPosition();

    final long fontSize = fontMetrics.getMaxHeight();
    final long threshold = (long) ( fontSize * 1.005 );
    final long safeFontSize = (long) ( fontSize * 1.3 );
    final long totalAscentAndDescent = fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent();
    if ( fontSize < totalAscentAndDescent  &&  totalAscentAndDescent  < threshold ) {
      return new DefaultExtendedBaselineInfo( dominantBaseline, baselineInfo, 0, 0, safeFontSize,
              safeFontSize, underlinePosition, strikeThroughPosition );
    }

    return new DefaultExtendedBaselineInfo( dominantBaseline, baselineInfo, 0, 0, fontSize,
            fontSize, underlinePosition, strikeThroughPosition );
  }
}
