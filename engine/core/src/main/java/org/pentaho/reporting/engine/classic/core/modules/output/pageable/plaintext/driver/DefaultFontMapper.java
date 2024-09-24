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
