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

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.pentaho.reporting.libraries.base.util.StringUtils.isEmpty;

/**
 * @author Andrey Khayrutdinov
 */
@RunWith( Parameterized.class )
public class CategoricalChartExpressionsConfigsTest {

  @BeforeClass
  public static void ensureBootIsDone() {
    ClassicEngineBoot.getInstance().start();
  }

  @Parameterized.Parameters
  public static List<Object[]> getClasses() {
    return Arrays.asList( new Object[][] {
      { LineChartExpression.class },
      { BarChartExpression.class },
      { BarLineChartExpression.class },
      { AreaChartExpression.class },
      { WaterfallChartExpressions.class }
    } );
  }

  private final String name;
  private final ExpressionMetaData expression;
  private final Locale locale;

  public CategoricalChartExpressionsConfigsTest( Class<? extends CategoricalChartExpression> clazz ) {
    name = clazz.getName();
    expression = ExpressionRegistry.getInstance().getExpressionMetaData( name );
    locale = Locale.getDefault();
  }

  @Test
  public void expressionLoaded() {
    assertNotNull( name, expression );
  }

  @Test
  public void typeIsDefined() {
    assertNotNull( name, expression.getExpressionType() );
  }

  @Test
  public void resultTypeIsDefined() {
    assertNotNull( name, expression.getResultType() );
  }

  @Test
  public void propertiesAreDefined() {
    ExpressionPropertyMetaData[] descriptions = expression.getPropertyDescriptions();
    for ( ExpressionPropertyMetaData description : descriptions ) {
      assertNotNull( description.getName() );
      assertFalse( description.getName(), isEmpty( description.getGrouping( locale ) ) );
    }
  }
}
