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
* Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
* All rights reserved.
*/

package org.pentaho.plugin.jfreereport.reportcharts.backport;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBlock;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.pentaho.reporting.libraries.formatting.FastMessageFormat;

import java.awt.*;
import java.util.Locale;

public class FormattedCategoryAxis extends CategoryAxis {
  private FastMessageFormat format;

  public FormattedCategoryAxis( final String label,
                                final String formatString,
                                final Locale locale ) {
    super( label );
    format = new FastMessageFormat( formatString, locale );
  }

  protected TextBlock createLabel( final Comparable category, final float width,
                                   final RectangleEdge edge, final Graphics2D g2 ) {
    return TextUtilities.createTextBlock( format.format( new Object[] { category } ),
      getTickLabelFont( category ), getTickLabelPaint( category ), width,
      getMaximumCategoryLabelLines(), new G2TextMeasurer( g2 ) );
  }
}
