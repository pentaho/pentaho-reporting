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

import javax.swing.*;
import java.awt.*;

public class FormulaParameterEntityCellEditor extends DefaultCellEditor {
  private FormulaParameterEntity entity;

  public FormulaParameterEntityCellEditor() {
    super( new JTextField() );
  }

  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    if ( value instanceof FormulaParameterEntity ) {
      this.entity = (FormulaParameterEntity) value;
      return super.getTableCellEditorComponent( table, entity.getName(), isSelected, row, column );
    }
    return super.getTableCellEditorComponent( table, null, isSelected, row, column );
  }

  public Object getCellEditorValue() {
    if ( this.entity == null ) {
      return null;
    }
    entity.setName( (String) super.getCellEditorValue() );
    return entity;
  }
}
