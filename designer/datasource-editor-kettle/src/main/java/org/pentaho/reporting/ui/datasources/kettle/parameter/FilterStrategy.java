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
package org.pentaho.reporting.ui.datasources.kettle.parameter;

import javax.swing.table.TableModel;

public interface FilterStrategy<T extends TableModel> {
  boolean isAcceptedRow( final int row, final T parentModel );
}
