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

package org.pentaho.reporting.engine.classic.core.layout.model;

public enum ValidationResult {
  UNKNOWN, OK, TABLE_BODY_NEEDS_MORE_ROWS, TABLE_ROW_OPEN, CANVAS_BOX_OPEN, BOX_OPEN_NEXT_PENDING, PLACEHOLDER_BOX_OPEN, PARAGRAPH_BOX_OPEN, CELL_BOX_OPEN, TABLE_BOX_OPEN, TABLE_BOX_MISSING_DATA, TABLE_BODY_MISSING_ROWS, TABLE_BOX_PREVENTS_PAGINATION,
}
