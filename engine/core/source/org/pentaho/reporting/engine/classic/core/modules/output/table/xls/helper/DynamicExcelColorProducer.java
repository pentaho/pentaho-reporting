package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class DynamicExcelColorProducer implements ExcelColorProducer
{
  private short lastUsedColor = 0x8; // this magic constant stands for first color index in a palette

  private HSSFWorkbook workbook;
  private Map usedTripplets;

  public DynamicExcelColorProducer(final HSSFWorkbook workbook)
  {
    if (workbook == null)
    {
      throw new NullPointerException();
    }
    this.workbook = workbook;
    this.usedTripplets = new HashMap();
  }

  public HSSFColor getColor(final short index)
  {
    final HSSFPalette palette = workbook.getCustomPalette();
    return palette.getColor(index);
  }

  public short getNearestColor(final Color awtColor)
  {
    if (lastUsedColor > 64)
    {
      // we ran out of palette... try to get nearest color then
      return StaticExcelColorSupport.getNearestColor(awtColor, usedTripplets);
    }

    final HSSFPalette palette = workbook.getCustomPalette();
    final HSSFColor hssfColor = palette.findColor((byte) awtColor.getRed(),
        (byte) awtColor.getGreen(),
        (byte) awtColor.getBlue());

    if (hssfColor != null && hssfColor.getIndex() < lastUsedColor)
    {
      return hssfColor.getIndex();
    }
    else
    {
      palette.setColorAtIndex(lastUsedColor, (byte) awtColor.getRed(),
          (byte) awtColor.getGreen(),
          (byte) awtColor.getBlue());
      final HSSFColor color = palette.getColor(lastUsedColor);
      usedTripplets.put(color.getHexString(), color);
      return lastUsedColor++;
    }
  }

}
