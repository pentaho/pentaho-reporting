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

import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.data.general.PieDataset;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.GenericExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.WrapperExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

public class FormulaPieTooltipGenerator implements PieToolTipGenerator {
  private FormulaExpression formulaExpression;
  private GenericExpressionRuntime runtime;
  private static final String[] ADDITIONAL_COLUMN_KEYS = new String[] {
    "chart::key", "chart::keys",
    "chart::item", "chart::items",
    "chart::pie-index"
  };

  public FormulaPieTooltipGenerator( final ExpressionRuntime runtime,
                                     final String formula ) {
    this.runtime = new GenericExpressionRuntime( runtime );
    this.formulaExpression = new FormulaExpression();
    this.formulaExpression.setFormula( formula );
  }

  /**
   * Generates a tool tip text item for the specified item in the dataset. This method can return <code>null</code> to
   * indicate that no tool tip should be displayed for an item.
   *
   * @param dataset the dataset (<code>null</code> not permitted).
   * @param key     the section key (<code>null</code> not permitted).
   * @return The tool tip text (possibly <code>null</code>).
   */
  public String generateToolTip( PieDataset dataset, Comparable key ) {
    try {
      final Object[] keys = dataset.getKeys().toArray();
      final Object[] items = new Object[ keys.length ];
      for ( int i = 0; i < keys.length; i++ ) {
        items[ i ] = dataset.getValue( i );
      }
      final Object[] values = new Object[] {
        key, keys,
        dataset.getValue( key ), items,
        IntegerCache.getInteger( dataset.getIndex( key ) )
      };
      formulaExpression.setRuntime( new WrapperExpressionRuntime
        ( new StaticDataRow( ADDITIONAL_COLUMN_KEYS, values ), runtime ) );
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
