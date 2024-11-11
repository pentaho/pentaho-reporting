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

import org.pentaho.reporting.libraries.designtime.swing.table.PropertyTableModel;

public interface ElementMetaDataTableModel extends SortableTableModel, PropertyTableModel {
  public String getValueRole( final int row, final int column );

  public String[] getExtraFields( final int row, final int column );
}
