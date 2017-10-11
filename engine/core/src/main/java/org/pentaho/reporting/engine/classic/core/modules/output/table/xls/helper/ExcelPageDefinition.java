/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
