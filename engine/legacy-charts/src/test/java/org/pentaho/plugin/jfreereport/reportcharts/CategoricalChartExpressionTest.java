/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.plugin.jfreereport.reportcharts;

import java.awt.Font;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.plugin.jfreereport.reportcharts.CategoricalChartExpression.PlaneDirection;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;

/**
 * @author Andrey Khayrutdinov
 */
public class CategoricalChartExpressionTest {

  /**
   * To make some methods public.
   */
  class TestableCategoricalChartExpression extends CategoricalChartExpression {

    @Override
    public PlaneDirection getTextAnchorDirectionOfAngle( double angle ) {
      return super.getTextAnchorDirectionOfAngle( angle );
    }

    @Override
    public CategoryLabelPosition createUpRotationCategoryLabelPosition( PlaneDirection axisPosition, double labelAngle ) {
      return super.createUpRotationCategoryLabelPosition( axisPosition, labelAngle );
    }

  }

  @BeforeClass
  public static void ensureBootIsDone() {
    ClassicEngineBoot.getInstance().start();
  }

  private CategoricalChartExpression expression;

  @Before
  public void setUp() {
    expression = new DummyCategoricalChartExpression();
    expression.setRuntime( new DebugExpressionRuntime() );
    expression.setAutoRange( false );
    expression.setScaleFactor( 1.1 );
  }

  @Test
  public void configureRangeAxis_PositiveValues() {
    final double lower = 10;
    final double upper = 20;
    NumberAxis axis = new NumberAxis();
    axis.setRange( lower, upper );

    expression.configureRangeAxis( createCategoryPlotWith( axis ), createFont() );

    Assert.assertTrue( axis.getLowerBound() < lower );
    Assert.assertTrue( axis.getLowerBound() > 0 );
    Assert.assertTrue( axis.getUpperBound() > upper );
  }

  @Test
  public void configureRangeAxis_NegativeValues() {
    final double lower = -20;
    final double upper = -10;
    NumberAxis axis = new NumberAxis();
    axis.setRange( lower, upper );

    expression.configureRangeAxis( createCategoryPlotWith( axis ), createFont() );

    Assert.assertTrue( axis.getLowerBound() < lower );
    Assert.assertTrue( axis.getUpperBound() > upper );
    Assert.assertTrue( axis.getUpperBound() < 0 );
  }

  @Test
  public void configureRangeAxis_AnyValues() {
    final double lower = -20;
    final double upper = 20;
    NumberAxis axis = new NumberAxis();
    axis.setRange( lower, upper );

    expression.configureRangeAxis( createCategoryPlotWith( axis ), createFont() );

    Assert.assertTrue( axis.getLowerBound() < lower );
    Assert.assertTrue( axis.getUpperBound() > upper );
  }


  private static CategoryPlot createCategoryPlotWith( ValueAxis rangeAxis ) {
    CategoryPlot plot = new CategoryPlot();
    plot.setRangeAxis( rangeAxis );
    return plot;
  }

  private static Font createFont() {
    return new Font( "Arial", Font.PLAIN, 12 );
  }

  @Test
  public void testGetTextAnchorDirectionOfAngle() {
    TestableCategoricalChartExpression e = new TestableCategoricalChartExpression();

    // Do not check exact change points, due to double-precision calculations don't provide sufficient accuracy.
    Assert.assertEquals( PlaneDirection.RIGHT, e.getTextAnchorDirectionOfAngle( 0.0 ) );
    Assert.assertEquals( PlaneDirection.RIGHT, e.getTextAnchorDirectionOfAngle( 45.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.RIGHT, e.getTextAnchorDirectionOfAngle( 56.0 * Math.PI / 180.0 ) );
    // change at 56.25 (5/32)
    Assert.assertEquals( PlaneDirection.TOP_RIGHT, e.getTextAnchorDirectionOfAngle( 57.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.TOP_RIGHT, e.getTextAnchorDirectionOfAngle( 78.0 * Math.PI / 180.0 ) );
    // change at 78.75 (7/32)
    Assert.assertEquals( PlaneDirection.TOP, e.getTextAnchorDirectionOfAngle( 79.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.TOP, e.getTextAnchorDirectionOfAngle( 90.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.TOP, e.getTextAnchorDirectionOfAngle( 101.0 * Math.PI / 180.0 ) );
    // change at 101.25 (9/32)
    Assert.assertEquals( PlaneDirection.TOP_LEFT, e.getTextAnchorDirectionOfAngle( 102.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.TOP_LEFT, e.getTextAnchorDirectionOfAngle( 123.0 * Math.PI / 180.0 ) );
    // change at 123.75 (11/32)
    Assert.assertEquals( PlaneDirection.LEFT, e.getTextAnchorDirectionOfAngle( 124.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.LEFT, e.getTextAnchorDirectionOfAngle( 180.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.LEFT, e.getTextAnchorDirectionOfAngle( 236.0 * Math.PI / 180.0 ) );
    // change at 236.25 (21/32)
    Assert.assertEquals( PlaneDirection.BOTTOM_LEFT, e.getTextAnchorDirectionOfAngle( 237.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.BOTTOM_LEFT, e.getTextAnchorDirectionOfAngle( 258.0 * Math.PI / 180.0 ) );
    // change at 258.75 (23/32)
    Assert.assertEquals( PlaneDirection.BOTTOM, e.getTextAnchorDirectionOfAngle( 259.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.BOTTOM, e.getTextAnchorDirectionOfAngle( 270.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.BOTTOM, e.getTextAnchorDirectionOfAngle( 281.0 * Math.PI / 180.0 ) );
    // change at 281.25 (25/32)
    Assert.assertEquals( PlaneDirection.BOTTOM_RIGHT, e.getTextAnchorDirectionOfAngle( 282.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.BOTTOM_RIGHT, e.getTextAnchorDirectionOfAngle( 303.0 * Math.PI / 180.0 ) );
    // change at 303.75 (27/32)
    Assert.assertEquals( PlaneDirection.RIGHT, e.getTextAnchorDirectionOfAngle( 304.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.RIGHT, e.getTextAnchorDirectionOfAngle( 315.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.RIGHT, e.getTextAnchorDirectionOfAngle( 360.0 * Math.PI / 180.0 ) );
    // check some negative angles
    Assert.assertEquals( PlaneDirection.BOTTOM, e.getTextAnchorDirectionOfAngle( -90.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.RIGHT, e.getTextAnchorDirectionOfAngle( -45.0 * Math.PI / 180.0 ) );
    Assert.assertEquals( PlaneDirection.LEFT, e.getTextAnchorDirectionOfAngle( -135.0 * Math.PI / 180.0 ) );
  }

  @Test
  public void testCreateUpRotationCategoryLabelPosition() {
    TestableCategoricalChartExpression e = new TestableCategoricalChartExpression();
    {
      CategoryLabelPosition c = e.createUpRotationCategoryLabelPosition( PlaneDirection.BOTTOM, 0.0 );
      Assert.assertEquals( RectangleAnchor.TOP, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.TOP_CENTER, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.TOP_CENTER, c.getRotationAnchor() );
    }
    {
      CategoryLabelPosition c = e.createUpRotationCategoryLabelPosition( PlaneDirection.BOTTOM, 90.0 * Math.PI / 180.0 );
      Assert.assertEquals( RectangleAnchor.TOP, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.CENTER_RIGHT, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.CENTER_RIGHT, c.getRotationAnchor() );
    }
    {
      CategoryLabelPosition c =
          e.createUpRotationCategoryLabelPosition( PlaneDirection.BOTTOM, -90.0 * Math.PI / 180.0 );
      Assert.assertEquals( RectangleAnchor.TOP, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.CENTER_LEFT, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.CENTER_LEFT, c.getRotationAnchor() );
    }
    {
      CategoryLabelPosition c =
          e.createUpRotationCategoryLabelPosition( PlaneDirection.BOTTOM, 180.0 * Math.PI / 180.0 );
      Assert.assertEquals( RectangleAnchor.TOP, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.BOTTOM_CENTER, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.BOTTOM_CENTER, c.getRotationAnchor() );
    }

    {
      CategoryLabelPosition c = e.createUpRotationCategoryLabelPosition( PlaneDirection.LEFT, 0.0 );
      Assert.assertEquals( RectangleAnchor.RIGHT, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.CENTER_RIGHT, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.CENTER_RIGHT, c.getRotationAnchor() );
    }
    {
      CategoryLabelPosition c = e.createUpRotationCategoryLabelPosition( PlaneDirection.LEFT, 90.0 * Math.PI / 180.0 );
      Assert.assertEquals( RectangleAnchor.RIGHT, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.BOTTOM_CENTER, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.BOTTOM_CENTER, c.getRotationAnchor() );
    }
    {
      CategoryLabelPosition c = e.createUpRotationCategoryLabelPosition( PlaneDirection.LEFT, -90.0 * Math.PI / 180.0 );
      Assert.assertEquals( RectangleAnchor.RIGHT, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.TOP_CENTER, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.TOP_CENTER, c.getRotationAnchor() );
    }
    {
      CategoryLabelPosition c = e.createUpRotationCategoryLabelPosition( PlaneDirection.LEFT, 180.0 * Math.PI / 180.0 );
      Assert.assertEquals( RectangleAnchor.RIGHT, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.CENTER_LEFT, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.CENTER_LEFT, c.getRotationAnchor() );
    }

    {
      CategoryLabelPosition c = e.createUpRotationCategoryLabelPosition( PlaneDirection.TOP, 0.0 );
      Assert.assertEquals( RectangleAnchor.BOTTOM, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.BOTTOM_CENTER, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.BOTTOM_CENTER, c.getRotationAnchor() );
    }
    {
      CategoryLabelPosition c = e.createUpRotationCategoryLabelPosition( PlaneDirection.TOP, 90.0 * Math.PI / 180.0 );
      Assert.assertEquals( RectangleAnchor.BOTTOM, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.CENTER_LEFT, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.CENTER_LEFT, c.getRotationAnchor() );
    }

    {
      CategoryLabelPosition c = e.createUpRotationCategoryLabelPosition( PlaneDirection.RIGHT, 0.0 );
      Assert.assertEquals( RectangleAnchor.LEFT, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.CENTER_LEFT, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.CENTER_LEFT, c.getRotationAnchor() );
    }
    {
      CategoryLabelPosition c = e.createUpRotationCategoryLabelPosition( PlaneDirection.RIGHT, 90.0 * Math.PI / 180.0 );
      Assert.assertEquals( RectangleAnchor.LEFT, c.getCategoryAnchor() );
      Assert.assertEquals( TextBlockAnchor.TOP_CENTER, c.getLabelAnchor() );
      Assert.assertEquals( TextAnchor.TOP_CENTER, c.getRotationAnchor() );
    }
  }

  @Test
  public void testStandardTickUnitsApplyFormat() throws Exception{
    NumberAxis axis = new NumberAxis();
    final TickUnits standardTickUnits = (TickUnits)axis.getStandardTickUnits();
    final double initialFirstTickUnitSize = standardTickUnits.get( 0 ).getSize();
    assertTickUnitSizeByPattern( "", initialFirstTickUnitSize);

    assertTickUnitSizeByPattern( "#,###", 1.0 );
    assertTickUnitSizeByPattern( "#", 1.0 );
    assertTickUnitSizeByPattern( "#.#", 0.1 );
    assertTickUnitSizeByPattern( "#.####", 1.0E-4 );
  }

  private void assertTickUnitSizeByPattern( String pattern, double tickUnitSize) {
    NumberAxis axis = new NumberAxis();
    DecimalFormat formatter = new DecimalFormat( pattern,  new DecimalFormatSymbols(new Locale( "en_US" ) ) );
    expression.standardTickUnitsApplyFormat( axis, formatter );
    final TickUnits standardTickUnits = (TickUnits)axis.getStandardTickUnits();
    // first n standard tick unit elements should be removed
    Assert.assertEquals( tickUnitSize, standardTickUnits.get( 0 ).getSize(), 0.0000000001 );
  }
}

