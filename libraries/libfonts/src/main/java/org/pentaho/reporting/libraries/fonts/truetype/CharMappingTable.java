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


package org.pentaho.reporting.libraries.fonts.truetype;

/**
 * Creation-Date: 06.11.2005, 20:24:42
 *
 * @author Thomas Morgner
 */
public class CharMappingTable implements FontTable {
  private static final long TABLE_ID =
    ( 'c' << 24 | 'm' << 16 | 'a' << 8 | 'p' );

  // no longer used ..
  private int version;
  private int numberOfSubTables;

  public CharMappingTable() {
  }

  public long getName() {
    return TABLE_ID;
  }
}
