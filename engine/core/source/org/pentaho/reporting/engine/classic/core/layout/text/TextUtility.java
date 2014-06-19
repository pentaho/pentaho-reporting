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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.layout.text;

import org.pentaho.reporting.libraries.fonts.registry.BaselineInfo;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

/**
 * Creation-Date: 04.04.2007, 14:46:14
 *
 * @author Thomas Morgner
 */
public class TextUtility
{
  private TextUtility()
  {
  }

  private static int translateBaselines(final int baseline)
  {
    switch (baseline)
    {
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
        throw new IllegalArgumentException("Invalid baseline");
    }
  }

  public static ExtendedBaselineInfo createBaselineInfo(final int codepoint,
                                                        final FontMetrics fontMetrics,
                                                        final BaselineInfo reusableBaselineInfo)
  {
    if (fontMetrics == null)
    {
      throw new NullPointerException("FontMetrics cannot be null");
    }

    final BaselineInfo baselineInfo = fontMetrics.getBaselines(codepoint, reusableBaselineInfo);
    final int dominantBaseline = TextUtility.translateBaselines(baselineInfo.getDominantBaseline());
    final long underlinePosition = fontMetrics.getUnderlinePosition();
    final long strikeThroughPosition = fontMetrics.getStrikeThroughPosition();
    return new DefaultExtendedBaselineInfo
        (dominantBaseline, baselineInfo, 0, 0, fontMetrics.getMaxHeight(),
            fontMetrics.getMaxHeight(), underlinePosition, strikeThroughPosition);
  }

}
