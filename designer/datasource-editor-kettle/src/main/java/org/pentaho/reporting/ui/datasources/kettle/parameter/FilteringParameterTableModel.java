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

package org.pentaho.reporting.ui.datasources.kettle.parameter;

import org.pentaho.reporting.libraries.designtime.swing.table.PropertyTableModel;
import org.pentaho.reporting.libraries.designtime.swing.table.RowMapperTableModel;

import java.beans.PropertyEditor;

public class FilteringParameterTableModel extends RowMapperTableModel implements PropertyTableModel {
  private FormulaParameterTableModel backend;
  private FilterStrategy<FormulaParameterTableModel> filterType;

  public FilteringParameterTableModel( final FilterStrategy<FormulaParameterTableModel> filterType,
                                       final FormulaParameterTableModel backend ) {
    super( backend );
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

  protected boolean isFiltered( final int row ) {
    return filterType.isAcceptedRow( row, backend ) == false;
  }

  public Class getClassForCell( final int row, final int col ) {
    return backend.getClassForCell( mapToModel( row ), col );
  }

  public PropertyEditor getEditorForCell( final int row, final int column ) {
    return backend.getEditorForCell( mapToModel( row ), column );
  }
}
