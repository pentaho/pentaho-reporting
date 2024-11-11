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
package org.pentaho.reporting.libraries.designtime.swing.table;

import javax.swing.table.TableModel;

public interface SortableTableModel extends TableModel {
  public void setTableStyle( final TableStyle tableStyle );

  public TableStyle getTableStyle();
}
