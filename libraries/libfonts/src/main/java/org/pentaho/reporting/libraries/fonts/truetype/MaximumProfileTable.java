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



package org.pentaho.reporting.libraries.fonts.truetype;

/**
 * Creation-Date: 06.11.2005, 20:24:42
 *
 * @author Thomas Morgner
 */
public class MaximumProfileTable implements FontTable {
  private static final long TABLE_ID =
    ( 'm' << 24 | 'a' << 16 | 'x' << 8 | 'p' );

  public MaximumProfileTable() {
  }

  public long getName() {
    return TABLE_ID;
  }
}
