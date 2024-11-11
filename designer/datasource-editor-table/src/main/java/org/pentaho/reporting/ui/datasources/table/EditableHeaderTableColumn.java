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


package org.pentaho.reporting.ui.datasources.table;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

public class EditableHeaderTableColumn extends TableColumn {
  private TableCellEditor headerEditor;
  private boolean isHeaderEditable;

  public EditableHeaderTableColumn( final int modelIndex ) {
    super( modelIndex );
    setHeaderEditor( createDefaultHeaderEditor() );
    isHeaderEditable = true;
  }

  public void setHeaderEditor( final TableCellEditor headerEditor ) {
    this.headerEditor = headerEditor;
  }

  public TableCellEditor getHeaderEditor() {
    return headerEditor;
  }

  public void setHeaderEditable( final boolean isEditable ) {
    isHeaderEditable = isEditable;
  }

  public boolean isHeaderEditable() {
    return isHeaderEditable;
  }

  protected TableCellEditor createDefaultHeaderEditor() {
    return new DefaultCellEditor( new JTextField() );
  }
}
