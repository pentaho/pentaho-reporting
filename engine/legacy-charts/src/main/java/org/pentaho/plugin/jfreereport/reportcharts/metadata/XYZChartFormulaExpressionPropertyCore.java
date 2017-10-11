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

package org.pentaho.plugin.jfreereport.reportcharts.metadata;

import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;

public class XYZChartFormulaExpressionPropertyCore extends DefaultExpressionPropertyCore {
  private static final String[] ADDITIONAL_COLUMN_KEYS = new String[] {
    "chart::x-value", "chart::y-value", "chart::z-value",
    "chart::series-index", "chart::series-key", "chart::series-count",
    "chart::item-index", "chart::item-count"
  };

  public XYZChartFormulaExpressionPropertyCore() {
  }

  public String[] getExtraCalculationFields( final ExpressionPropertyMetaData metaData ) {
    return ADDITIONAL_COLUMN_KEYS.clone();
  }
}
