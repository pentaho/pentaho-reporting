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


package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;

public interface ExcelColorProducer {
  // public HSSFColor getColor(final short index);

  /**
   * Returns the nearest indexed color for the palette (if palettes are used) or -1 if no palette is used.
   *
   * @param awtColor
   * @return
   */
  public short getNearestColor( final Color awtColor );

}
