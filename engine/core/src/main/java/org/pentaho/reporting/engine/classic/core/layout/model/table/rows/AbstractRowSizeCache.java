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

public class AbstractRowSizeCache {
  private long[] validatedSizes;
  private int validateSizesFillState;

  public AbstractRowSizeCache() {
  }

  private int computeMaxArraySize( long[] array, int rowCount ) {
    if ( array == null ) {
      return rowCount;
    }
    return Math.max( rowCount, array.length + 2000 );
  }

  protected long[] get( BulkArrayList<TableRowImpl> rows ) {
    int rowCount = rows.size();
    if ( this.validatedSizes == null || this.validatedSizes.length < rowCount ) {
      int growth = computeMaxArraySize( this.validatedSizes, rowCount );
      long[] newValidatedSizes = new long[growth];
      if ( this.validatedSizes != null ) {
        System.arraycopy( this.validatedSizes, 0, newValidatedSizes, 0, this.validatedSizes.length );
      }
      this.validatedSizes = newValidatedSizes;
    }

    return validatedSizes;
  }

  public void setFillState( final int validateSizesFillState ) {
    this.validateSizesFillState = validateSizesFillState;
  }

  public int getFillState() {
    return validateSizesFillState;
  }
}
