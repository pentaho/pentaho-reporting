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

package org.pentaho.reporting.engine.classic.core.filter;

/**
 * The FormatSpecification classifies a format string into the 4 known classes of format-strings of Java. This is used
 * by the Excel-export to transform the raw-formatstring into a suitable cell-format.
 * <p/>
 * This class is plain value-carrier. It is mutable and should not be used outside the scope of querying
 * raw-datasources.
 *
 * @author : Thomas Morgner
 */
public class FormatSpecification {
  /**
   * A constant declaring that the format-type cannot be determined in a reliable way.
   */
  public static final int TYPE_UNDEFINED = 0;
  /**
   * A constant declaring that the format-type is a simple date-format.
   */
  public static final int TYPE_DATE_FORMAT = 1;
  /**
   * A constant declaring that the format-type is a decimal-format.
   */
  public static final int TYPE_DECIMAL_FORMAT = 2;
  /**
   * A constant declaring that the format-type is a message-format.
   */
  public static final int TYPE_MESSAGE_FORMAT = 3;
  /**
   * A constant declaring that the format-type is a choice-format.
   */
  public static final int TYPE_CHOICE_FORMAT = 4;
  /**
   * The format type as one of the constants defined in this class.
   */
  private int type;
  /**
   * The raw java format string.
   */
  private String formatString;

  /**
   * Creates an empty object. This object must be filled with valid values by calling {@link #redefine(int, String)}
   * later.
   */
  public FormatSpecification() {
  }

  /**
   * Redefines the values stored in this specification object.
   *
   * @param type
   *          the type, one of the constants declared in this class.
   * @param formatString
   *          the format string.
   */
  public void redefine( final int type, final String formatString ) {
    this.type = type;
    this.formatString = formatString;
  }

  /**
   * Returns the type of the format string contained in this class.
   *
   * @return the type.
   */
  public int getType() {
    return type;
  }

  /**
   * Returns the raw-format-string.
   *
   * @return the format string.
   */
  public String getFormatString() {
    return formatString;
  }
}
