package org.pentaho.reporting.ui.datasources.kettle.parameter;

import org.pentaho.reporting.libraries.designtime.swing.table.GroupingModel;

public interface FilterStrategy<T extends GroupingModel>
{
  boolean isAcceptedRow(final int row, final T parentModel);
}
