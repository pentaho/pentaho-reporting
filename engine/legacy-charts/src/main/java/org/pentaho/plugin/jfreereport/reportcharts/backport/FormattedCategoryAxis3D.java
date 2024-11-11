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


package org.pentaho.plugin.jfreereport.reportcharts.backport;

import org.jfree.chart.axis.CategoryAxis3D;
import org.jfree.text.G2TextMeasurer;
import org.jfree.text.TextBlock;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.pentaho.reporting.libraries.formatting.FastMessageFormat;

import java.awt.*;
import java.util.Locale;

public class FormattedCategoryAxis3D extends CategoryAxis3D {
  private FastMessageFormat format;

  public FormattedCategoryAxis3D( final String label,
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
