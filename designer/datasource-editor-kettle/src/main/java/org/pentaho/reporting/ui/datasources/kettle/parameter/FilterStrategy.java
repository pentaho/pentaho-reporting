package org.pentaho.reporting.ui.datasources.kettle.parameter;

import javax.swing.table.TableModel;

public interface FilterStrategy<T extends TableModel> {
  boolean isAcceptedRow( final int row, final T parentModel );
}
