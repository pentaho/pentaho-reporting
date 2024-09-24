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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver;

/**
 * Creation-Date: 13.05.2007, 15:28:24
 *
 * @author Thomas Morgner
 */
public class PrinterDriverCommands {
  /**
   * the roman font.
   */
  public static final byte SELECT_FONT_ROMAN = 0x00;
  /**
   * the swiss font.
   */
  public static final byte SELECT_FONT_SWISS = 0x01;
  /**
   * the courier font.
   */
  public static final byte SELECT_FONT_COURIER = 0x02;
  /**
   * the prestige font.
   */
  public static final byte SELECT_FONT_PRESTIGE = 0x03;
  /**
   * the OCR-A font.
   */
  public static final byte SELECT_FONT_OCR_A = 0x05;
  /**
   * the OCR-B font.
   */
  public static final byte SELECT_FONT_OCR_B = 0x06;
  /**
   * the orator font.
   */
  public static final byte SELECT_FONT_ORATOR = 0x07;
  /**
   * the swiss-bold font.
   */
  public static final byte SELECT_FONT_SWISS_BOLD = 0x7A;
  /**
   * the gothic font.
   */
  public static final byte SELECT_FONT_GOTHIC = 0x7C;
  /**
   * selects the font, which is selected on the printer menu.
   */
  public static final byte SELECT_FONT_FROM_MENU = 0x7F;
  /**
   * the Carriage Return control character, the printer carriage returns to the start of the line.
   */
  public static final char CARRIAGE_RETURN = 0x0D;
  /**
   * scrolls the paper up a single line.
   */
  public static final char LINE_FEED = 0x0A;
  /**
   * the form feed character, ejects the current page and starts the next page.
   */
  public static final char FORM_FEED = 0x0C;
  /**
   * the space character.
   */
  public static final char SPACE = 0x20;

  public static final float CPI_10 = 10;
  public static final float CPI_12 = 12;
  public static final float CPI_15 = 15;
  public static final float CPI_17 = 17.14f;
  public static final float CPI_20 = 20;

  public static final float LPI_10 = 10;
  public static final float LPI_6 = 6;

  private PrinterDriverCommands() {
  }
}
