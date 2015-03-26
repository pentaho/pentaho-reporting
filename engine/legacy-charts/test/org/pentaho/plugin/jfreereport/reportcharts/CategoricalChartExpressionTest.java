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
 *  Copyright (c) 2006 - 2015 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.plugin.jfreereport.reportcharts;

import java.awt.Font;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;

/**
 * @author Andrey Khayrutdinov
 */
public class CategoricalChartExpressionTest
{
  @BeforeClass
  public static void ensureBootIsDone()
  {
    ClassicEngineBoot.getInstance().start();
  }

  private CategoricalChartExpression expression;

  @Before
  public void setUp()
  {
    expression = new DummyCategoricalChartExpression();
    expression.setRuntime(new DebugExpressionRuntime());
    expression.setAutoRange(false);
    expression.setScaleFactor(1.1);
  }

  @Test
  public void configureRangeAxis_PositiveValues()
  {
    final double lower = 10;
    final double upper = 20;
    NumberAxis axis = new NumberAxis();
    axis.setRange(lower, upper);

    expression.configureRangeAxis(createCategoryPlotWith(axis), createFont());

    assertTrue(axis.getLowerBound() < lower);
    assertTrue(axis.getLowerBound() > 0);
    assertTrue(axis.getUpperBound() > upper);
  }

  @Test
  public void configureRangeAxis_NegativeValues()
  {
    final double lower = -20;
    final double upper = -10;
    NumberAxis axis = new NumberAxis();
    axis.setRange(lower, upper);

    expression.configureRangeAxis(createCategoryPlotWith(axis), createFont());

    assertTrue(axis.getLowerBound() < lower);
    assertTrue(axis.getUpperBound() > upper);
    assertTrue(axis.getUpperBound() < 0);
  }

  @Test
  public void configureRangeAxis_AnyValues()
  {
    final double lower = -20;
    final double upper = 20;
    NumberAxis axis = new NumberAxis();
    axis.setRange(lower, upper);

    expression.configureRangeAxis(createCategoryPlotWith(axis), createFont());

    assertTrue(axis.getLowerBound() < lower);
    assertTrue(axis.getUpperBound() > upper);
  }


  private static CategoryPlot createCategoryPlotWith(ValueAxis rangeAxis)
  {
    CategoryPlot plot = new CategoryPlot();
    plot.setRangeAxis(rangeAxis);
    return plot;
  }

  private static Font createFont()
  {
    return new Font("Arial", Font.PLAIN, 12);
  }
}