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

import org.pentaho.reporting.libraries.fonts.FontMappingUtility;

import java.util.HashMap;

public class DefaultFontMapper implements FontMapper {
  private HashMap fontMapping;
  private byte defaultFont;

  public DefaultFontMapper() {
    fontMapping = new HashMap();
    defaultFont = PrinterDriverCommands.SELECT_FONT_ROMAN;
  }

  public void addFontMapping( final String fontName, final byte printerCode ) {
    if ( fontName == null ) {
      throw new NullPointerException();
    }
    fontMapping.put( fontName, new Byte( printerCode ) );
  }

  public void removeFontMapping( final String fontName ) {
    fontMapping.remove( fontName );
  }

  public byte getPrinterFont( final String fontName ) {
    final Byte b = (Byte) fontMapping.get( fontName );
    if ( b != null ) {
      return b.byteValue();
    }
    return handleDefault( fontName );
  }

  protected byte handleDefault( final String fd ) {
    if ( FontMappingUtility.isCourier( fd ) ) {
      return PrinterDriverCommands.SELECT_FONT_COURIER;
    }
    if ( FontMappingUtility.isSerif( fd ) ) {
      return PrinterDriverCommands.SELECT_FONT_ROMAN;
    }
    if ( FontMappingUtility.isSansSerif( fd ) ) {
      return PrinterDriverCommands.SELECT_FONT_OCR_A;
    }
    return defaultFont;
  }

  public byte getDefaultFont() {
    return defaultFont;
  }

  public void setDefaultFont( final byte defaultFont ) {
    this.defaultFont = defaultFont;
  }
}
