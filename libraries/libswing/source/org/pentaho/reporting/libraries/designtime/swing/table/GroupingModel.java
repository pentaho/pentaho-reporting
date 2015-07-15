package org.pentaho.reporting.libraries.designtime.swing.table;

import javax.swing.table.TableModel;

public interface GroupingModel extends TableModel {
  public GroupingHeader getGroupHeader( int index );

  public boolean isHeaderRow( int index );
}
