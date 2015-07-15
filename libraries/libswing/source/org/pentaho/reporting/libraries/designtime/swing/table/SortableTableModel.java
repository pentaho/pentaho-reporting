package org.pentaho.reporting.libraries.designtime.swing.table;

import javax.swing.table.TableModel;

public interface SortableTableModel extends TableModel {
  public void setTableStyle( final TableStyle tableStyle );

  public TableStyle getTableStyle();
}
