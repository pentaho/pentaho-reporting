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

package org.pentaho.reporting.libraries.fonts.itext;

import com.lowagie.text.pdf.BaseFont;

/**
 * A PDF font record. The record is used to cache the generated PDF fonts. Once created the base font record is
 * immutable. The base font record does not store font sizes.
 *
 * @author Thomas Morgner
 */
public final class BaseFontRecord {
  /**
   * The iText base font.
   */
  private BaseFont baseFont;

  /**
   * The file name.
   */
  private String fileName;

  /**
   * A flag indicating whether this font record describes an embedded PDF font.
   */
  private boolean embedded;
  // give me a marker to know whether to apply manual bold and italics styles ..
  private boolean trueTypeFont;

  private transient BaseFontRecordKey key;
  private boolean bold;
  private boolean italics;

  /**
   * Creates a new font record.
   *
   * @param fileName the physical filename name of the font file.
   * @param embedded a flag that defines whether this font should be embedded in the target document.
   * @param baseFont the generated base font for the given font definition.
   */
  public BaseFontRecord( final String fileName,
                         final boolean trueTypeFont,
                         final boolean embedded,
                         final BaseFont baseFont,
                         final boolean bold,
                         final boolean italics ) {
    if ( baseFont == null ) {
      throw new NullPointerException( "iText-FontDefinition is null." );
    }
    if ( fileName == null ) {
      throw new NullPointerException( "Logical font name is null." );
    }
    this.trueTypeFont = trueTypeFont;
    this.baseFont = baseFont;
    this.fileName = fileName;
    this.embedded = embedded;
    this.italics = italics;
    this.bold = bold;
  }

  public boolean isTrueTypeFont() {
    return trueTypeFont;
  }

  public boolean isBold() {
    return bold;
  }

  public boolean isItalics() {
    return italics;
  }

  /**
   * Creates a font record key.
   *
   * @return the font record key.
   */
  public BaseFontRecordKey createKey() {
    if ( key == null ) {
      key = new BaseFontRecordKey( getFileName(), getEncoding(), isEmbedded() );
    }
    return key;
  }

  /**
   * Returns the encoding.
   *
   * @return the encoding.
   */
  public String getEncoding() {
    return baseFont.getEncoding();
  }

  /**
   * Returns true if the font should be embedded in the PDF output, and false if not.
   *
   * @return true or false.
   */
  public boolean isEmbedded() {
    return embedded;
  }

  /**
   * Returns the logical name of the font.
   *
   * @return the logical name.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Returns the iText BaseFont.
   *
   * @return the itext BaseFont.
   */
  public BaseFont getBaseFont() {
    return baseFont;
  }

}
