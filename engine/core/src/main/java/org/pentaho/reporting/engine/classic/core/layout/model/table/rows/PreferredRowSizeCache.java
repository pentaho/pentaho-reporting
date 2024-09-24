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

package org.pentaho.reporting.engine.classic.core.layout.model.table.rows;

import org.pentaho.reporting.engine.classic.core.util.BulkArrayList;

public class PreferredRowSizeCache extends AbstractRowSizeCache {
  public PreferredRowSizeCache() {
  }

  public long[] get( final int limit, final BulkArrayList<TableRowImpl> rows ) {
    final long[] validatedSizes = get( rows );
    rows.foreach( new BulkArrayList.Func<TableRowImpl>() {
      public void process( final TableRowImpl value, final int index ) {
        validatedSizes[index] = value.getPreferredSize();
      }
    }, getFillState(), limit );
    return validatedSizes;
  }

  public void apply( final long[] trailingSizes, final int start, final int end, BulkArrayList<TableRowImpl> rows ) {
    rows.foreach( new BulkArrayList.Func<TableRowImpl>() {
      public void process( final TableRowImpl row, final int i ) {
        final long validateSize = trailingSizes[i];
        row.setPreferredSize( validateSize );
      }
    }, start, end );

    setFillState( end );
  }

}
