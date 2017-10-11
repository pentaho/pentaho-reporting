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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.crosstab;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

import java.io.Serializable;

public interface CrosstabSpecification extends Serializable {
  public ReportStateKey getKey();

  public void startRow();

  public void endRow();

  public void add( final DataRow dataRow );

  public String[] getColumnDimensionNames();

  public String[] getRowDimensionNames();

  public Object[] getKeyAt( int column );

  public int indexOf( final int startPosition, Object[] key );

  /**
   * The number of columns encountered.
   *
   * @return
   */
  public int size();

  public void endCrosstab();
}
