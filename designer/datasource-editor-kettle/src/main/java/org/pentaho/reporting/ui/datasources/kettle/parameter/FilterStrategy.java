/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

package org.pentaho.reporting.ui.datasources.kettle.parameter;

import javax.swing.table.TableModel;

public interface FilterStrategy<T extends TableModel> {
  boolean isAcceptedRow( final int row, final T parentModel );
}
