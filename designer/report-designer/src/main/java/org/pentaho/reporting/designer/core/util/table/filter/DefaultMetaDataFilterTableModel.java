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


package org.pentaho.reporting.designer.core.util.table.filter;

import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTableModel;
import org.pentaho.reporting.designer.core.util.table.GroupingHeader;
import org.pentaho.reporting.designer.core.util.table.GroupingModel;
import org.pentaho.reporting.designer.core.util.table.TableStyle;

import java.beans.PropertyEditor;

public class DefaultMetaDataFilterTableModel
  extends DefaultFilterTableModel implements ElementMetaDataTableModel, GroupingModel {
  private ElementMetaDataTableModel metaParent;
  private GroupingModel groupingBackend;

  public DefaultMetaDataFilterTableModel( final ElementMetaDataTableModel backend, final int filterColumn ) {
    super( backend, filterColumn );
    if ( backend instanceof GroupingModel ) {
      this.groupingBackend = (GroupingModel) backend;
    }
    this.metaParent = backend;
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
    applyFilter();
  }

  public TableStyle getTableStyle() {
    return metaParent.getTableStyle();
  }

  public GroupingHeader getGroupHeader( final int index ) {
    if ( groupingBackend == null ) {
      return null;
    }
    return groupingBackend.getGroupHeader( mapToModel( index ) );
  }

  public boolean isHeaderRow( final int index ) {
    if ( groupingBackend == null ) {
      return false;
    }
    return groupingBackend.isHeaderRow( mapToModel( index ) );
  }
}
