/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.util.table;

import org.pentaho.reporting.designer.core.editor.ReportRenderContext;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import java.awt.*;

public class StringValueCellEditor extends AbstractStringValueCellEditor implements TableCellEditor {
  public StringValueCellEditor() {
  }

  /**
   * Sets an initial <code>value</code> for the editor.  This will cause the editor to <code>stopEditing</code> and lose
   * any partially edited value if the editor is editing when this method is called. <p>
   * <p/>
   * Returns the component that should be added to the client's <code>Component</code> hierarchy.  Once installed in the
   * client's hierarchy this component will then be able to draw and receive user input.
   *
   * @param table      the <code>JTable</code> that is asking the editor to edit; can be <code>null</code>
   * @param value      the value of the cell to be edited; it is up to the specific editor to interpret and draw the
   *                   value.  For example, if value is the string "true", it could be rendered as a string or it could
   *                   be rendered as a check box that is checked.  <code>null</code> is a valid value
   * @param isSelected true if the cell is to be rendered with highlighting
   * @param row        the row of the cell being edited
   * @param column     the column of the cell being edited
   * @return the component for editing
   */
  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    removeAll();

    final TableModel tableModel = table.getModel();
    final ReportRenderContext reportContext = getReportContext();
    final String valueRole;
    final String[] extraFields;
    if ( reportContext != null ) {
      if ( tableModel instanceof ElementMetaDataTableModel ) {
        final ElementMetaDataTableModel metaDataTableModel = (ElementMetaDataTableModel) tableModel;
        valueRole = metaDataTableModel.getValueRole( row, column );
        extraFields = metaDataTableModel.getExtraFields( row, column );
      } else {
        valueRole = getValueRole();
        extraFields = EMPTY_EXTRA_FIELDS;
      }
    } else {
      valueRole = null;
      extraFields = getExtraFields();
    }
    return create( valueRole, extraFields, value );
  }

}
