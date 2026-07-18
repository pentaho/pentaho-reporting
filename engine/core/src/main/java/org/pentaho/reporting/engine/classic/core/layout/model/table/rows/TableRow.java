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



package org.pentaho.reporting.engine.classic.core.layout.model.table.rows;

import org.pentaho.reporting.engine.classic.core.layout.model.Border;

public interface TableRow {
  long getPreferredSize();

  long getPreferredSize( int colspan );

  long getValidatedLeadingSize();

  long getValidatedTrailingSize( int rowSpan );

  int getMaxValidatedRowSpan();

  long getValidateSize();

  Border getBorder();

  int getMaximumRowSpan();
}
