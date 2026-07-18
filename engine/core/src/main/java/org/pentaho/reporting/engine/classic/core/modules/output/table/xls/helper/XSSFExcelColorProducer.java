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



package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;

import org.apache.poi.xssf.usermodel.XSSFColor;

public class XSSFExcelColorProducer implements ExcelColorProducer {
  public XSSFExcelColorProducer() {
  }

  public short getNearestColor( final Color awtColor ) {
    XSSFColor color = new XSSFColor( awtColor, null );
    return color.getIndexed();
  }
}
