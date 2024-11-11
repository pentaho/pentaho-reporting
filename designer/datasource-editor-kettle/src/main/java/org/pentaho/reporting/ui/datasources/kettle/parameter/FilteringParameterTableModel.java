/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
