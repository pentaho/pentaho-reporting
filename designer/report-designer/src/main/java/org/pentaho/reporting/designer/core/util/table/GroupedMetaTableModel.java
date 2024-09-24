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
