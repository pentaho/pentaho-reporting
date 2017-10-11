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

package org.pentaho.reporting.designer.core.editor.drilldown;

import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.designer.core.util.table.GroupedMetaTableModel;

/**
 * Todo: Document me!
 * <p/>
 * Date: 15.09.2010 Time: 17:44:42
 *
 * @author Thomas Morgner.
 */
public class FilteringParameterTableModel extends GroupedMetaTableModel {
  private DrillDownParameterTableModel backend;
  private DrillDownParameter.Type filterType;
  private boolean onlyPreferredOptions;

  public FilteringParameterTableModel( final DrillDownParameter.Type filterType,
                                       final DrillDownParameterTableModel backend ) {
    this( filterType, backend, false );
  }

  public FilteringParameterTableModel( final DrillDownParameter.Type filterType,
                                       final DrillDownParameterTableModel backend,
                                       final boolean onlyPreferredOptions ) {
    super( backend );
    this.onlyPreferredOptions = onlyPreferredOptions;
    if ( filterType == null ) {
      throw new NullPointerException();
    }
    if ( backend == null ) {
      throw new NullPointerException();
    }

    this.filterType = filterType;
    this.backend = backend;
    recomputeRowCount();
  }

  protected void recomputeRowCount() {
    if ( backend == null ) {
      return;
    }
    super.recomputeRowCount();
  }

  public int mapToModel( final int row ) {
    final int size = backend.getRowCount();
    int effectiveRow = 0;
    for ( int i = 0; i < size; i++ ) {
      if ( isAcceptedRow( i ) ) {
        if ( effectiveRow == row ) {
          return i;
        }

        effectiveRow += 1;
      }

    }
    throw new IndexOutOfBoundsException( "Unable to map row to model: " + row );
  }

  protected boolean isAcceptedRow( final int row ) {
    final DrillDownParameter.Type groupHeader = backend.getParameterType( row );
    if ( groupHeader == filterType ) {
      if ( onlyPreferredOptions ) {
        return backend.isPreferred( row );
      }
      return true;
    }
    return false;
  }

  public int mapFromModel( final int row ) {
    if ( row < 0 ) {
      return row;
    }

    final int size = backend.getRowCount();
    int retval = 0;
    for ( int i = 0; i < size; i++ ) {
      if ( isAcceptedRow( i ) ) {
        if ( row == i ) {
          return retval;
        }
        retval += 1;
      }
    }
    return retval;
  }
}
