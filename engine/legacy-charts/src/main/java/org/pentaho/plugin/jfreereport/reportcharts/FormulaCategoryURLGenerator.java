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

import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.GenericExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.WrapperExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

public class FormulaCategoryURLGenerator implements CategoryURLGenerator {
  private FormulaExpression formulaExpression;

  private GenericExpressionRuntime runtime;

  private static final String[] ADDITIONAL_COLUMN_KEYS = new String[] { "chart::series-key", "chart::category-key",
    "chart::series-index", "chart::category-index", "chart::series-keys", "chart::category-keys", "chart::value" };

  public FormulaCategoryURLGenerator( final ExpressionRuntime runtime, final String formula ) {
    this.runtime = new GenericExpressionRuntime( runtime );
    this.formulaExpression = new FormulaExpression();
    this.formulaExpression.setFormula( formula );
  }

  /**
   * Returns a URL for one item in a dataset. As a guideline, the URL should be valid within the context of an XHTML 1.0
   * document.  Classes that implement this interface are responsible for correctly escaping any text that is derived
   * from the dataset, as this may be user-specified and could pose a security risk.
   *
   * @param dataset  the dataset.
   * @param series   the series (zero-based index).
   * @param category the category.
   * @return A string containing the URL.
   */
  public String generateURL( final CategoryDataset dataset, final int series, final int category ) {
    try {
      final Comparable seriesKey = dataset.getRowKey( series );
      final Comparable categoryKey = dataset.getColumnKey( category );
      final Object[] categoryKeys = dataset.getColumnKeys().toArray();
      final Object[] seriesKeys = dataset.getRowKeys().toArray();
      final Object value = dataset.getValue( series, category );

      final Object[] values = new Object[] { seriesKey, categoryKey, IntegerCache.getInteger( series ),
        IntegerCache.getInteger( category ), seriesKeys, categoryKeys, value };
      final StaticDataRow datarow = new StaticDataRow( ADDITIONAL_COLUMN_KEYS, values );
      final WrapperExpressionRuntime wrapper = new WrapperExpressionRuntime( datarow, runtime );
      formulaExpression.setRuntime( wrapper );
      final Object o = formulaExpression.getValue();
      if ( o == null ) {
        return null;
      }
      return String.valueOf( o );
    } finally {
      formulaExpression.setRuntime( null );
    }
  }
}
