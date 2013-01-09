package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;

public interface ExcelColorProducer
{
//  public HSSFColor getColor(final short index);

  /**
   * Returns the nearest indexed color for the palette (if palettes are used) or
   * -1 if no palette is used.
   *
   * @param awtColor
   * @return
   */
  public short getNearestColor(final Color awtColor);


}
