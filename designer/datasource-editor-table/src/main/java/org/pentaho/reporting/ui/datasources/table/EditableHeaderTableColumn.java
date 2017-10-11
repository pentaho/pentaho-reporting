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
