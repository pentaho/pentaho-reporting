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

package org.pentaho.reporting.designer.core.util.table;

import java.beans.PropertyEditor;

public class GroupedMetaTableModel extends GroupedTableModel implements ElementMetaDataTableModel {
  private ElementMetaDataTableModel metaParent;

  public GroupedMetaTableModel( final GroupingModel parent ) {
    super( parent );
    if ( parent instanceof ElementMetaDataTableModel == false ) {
      throw new IllegalArgumentException();
    }
    this.metaParent = (ElementMetaDataTableModel) parent;
  }

  public String[] getExtraFields( final int row, final int column ) {
    return metaParent.getExtraFields( mapToModel( row ), column );
  }

  public Class getClassForCell( final int row, final int column ) {
    return metaParent.getClassForCell( mapToModel( row ), column );
  }

  public PropertyEditor getEditorForCell( final int row, final int column ) {
    return metaParent.getEditorForCell( mapToModel( row ), column );
  }

  public String getValueRole( final int row, final int column ) {
    return metaParent.getValueRole( mapToModel( row ), column );
  }

  public void setTableStyle( final TableStyle tableStyle ) {
    metaParent.setTableStyle( tableStyle );
  }

  public TableStyle getTableStyle() {
    return metaParent.getTableStyle();
  }
}
