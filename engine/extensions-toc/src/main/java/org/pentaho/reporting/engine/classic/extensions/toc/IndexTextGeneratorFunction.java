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

package org.pentaho.reporting.engine.classic.extensions.toc;

/**
 * A data-collector that collects table-of-contents items at group-starts. The function collects these items accross
 * subreport boundaries.
 *
 * @author Thomas Morgner.
 */
public class IndexTextGeneratorFunction extends IndexNumberGeneratorFunction {
  private String indexSeparator;

  private boolean condensedStyle;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public IndexTextGeneratorFunction() {
    this.indexSeparator = ".";
  }

  public String getIndexSeparator() {
    return indexSeparator;
  }

  public void setIndexSeparator( final String indexSeparator ) {
    this.indexSeparator = indexSeparator;
  }

  public boolean isCondensedStyle() {
    return condensedStyle;
  }

  public void setCondensedStyle( final boolean condensedStyle ) {
    this.condensedStyle = condensedStyle;
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Integer[] groupCount = (Integer[]) super.getValue();
    if ( condensedStyle ) {
      return IndexUtility.getCondensedIndexText( groupCount, indexSeparator );
    }
    return IndexUtility.getIndexText( groupCount, indexSeparator );
  }
}
