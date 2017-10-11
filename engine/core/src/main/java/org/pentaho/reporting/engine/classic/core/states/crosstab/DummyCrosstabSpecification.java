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

public class DummyCrosstabSpecification implements CrosstabSpecification {
  private ReportStateKey key;
  private static final String[] EMPTY_NAMES = new String[0];
  private static final Object[] EMPTY_ROWS = new Object[0];

  public DummyCrosstabSpecification( final ReportStateKey key ) {
    this.key = key;
  }

  public ReportStateKey getKey() {
    return key;
  }

  public void startRow() {

  }

  public void endRow() {

  }

  public void add( final DataRow dataRow ) {

  }

  public void endCrosstab() {

  }

  public String[] getColumnDimensionNames() {
    return EMPTY_NAMES;
  }

  public String[] getRowDimensionNames() {
    return EMPTY_NAMES;
  }

  public Object[] getKeyAt( final int column ) {
    return EMPTY_ROWS;
  }

  public int indexOf( final int startPosition, final Object[] key ) {
    return -1;
  }

  public int size() {
    return 0;
  }
}
