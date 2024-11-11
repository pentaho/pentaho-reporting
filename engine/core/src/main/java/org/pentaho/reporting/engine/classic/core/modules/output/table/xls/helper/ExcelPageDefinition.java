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

import org.pentaho.reporting.engine.classic.core.util.PageSize;

/**
 * A read only page format mapping definiton to map a page format to an predefined excel constant.
 *
 * @author Thomas Morgner
 */
public final class ExcelPageDefinition {
  /**
   * The excel internal page format code referring to that page size.
   */
  private final short pageFormatCode;
  /**
   * The width of the page format.
   */
  private final int width;
  /**
   * The height of the page format.
   */
  private final int height;

  /**
   * Defines a new excel page format mapping.
   *
   * @param pageFormatCode
   *          the excel internal page format code.
   * @param width
   *          the width of the page.
   * @param height
   *          the height of the page.
   */
  public ExcelPageDefinition( final short pageFormatCode, final int width, final int height ) {
    this.pageFormatCode = pageFormatCode;
    this.width = width;
    this.height = height;
  }

  public ExcelPageDefinition( final short pageFormatCode, final PageSize pageSize ) {
    this( pageFormatCode, (int) pageSize.getWidth(), (int) pageSize.getHeight() );
  }

  /**
   * Return the excel page format code that describes that page size.
   *
   * @return the page format code as defined in the Excel File format.
   */
  public short getPageFormatCode() {
    return pageFormatCode;
  }

  /**
   * Returns the defined page width for that page definition.
   *
   * @return the page width;
   */
  public int getWidth() {
    return width;
  }

  /**
   * Returns the defined page height for that page definition.
   *
   * @return the page height;
   */
  public int getHeight() {
    return height;
  }
}
