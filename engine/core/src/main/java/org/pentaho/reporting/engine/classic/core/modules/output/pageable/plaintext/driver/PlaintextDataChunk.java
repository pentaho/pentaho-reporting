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
 * A data carrier to collect and store text data for the output.
 */
public class PlaintextDataChunk {
  /**
   * The text that should be printed.
   */
  private final String text;

  /**
   * The font definition stores the font style.
   */
  private final String font;

  private boolean bold;

  private boolean italic;

  /**
   * the column where the text starts.
   */
  private final int x;

  /**
   * the row of the text.
   */
  private final int y;

  /**
   * the text width.
   */
  private final int width;
  private boolean underline;
  private boolean strikethrough;

  /**
   * Creates a new text data chunk.
   *
   * @param text
   *          the text that should be printed
   * @param font
   *          the font style for the text
   * @param underline
   * @param strikethrough
   * @param x
   *          the column where the text starts
   * @param y
   *          the row of the text
   * @param w
   *          the number of characters of the text that should be printed.
   */
  protected PlaintextDataChunk( final String text, final String font, final boolean bold, final boolean italic,
      final boolean underline, final boolean strikethrough, final int x, final int y, final int w ) {
    if ( font == null ) {
      throw new NullPointerException( "Font must not be null" );
    }
    if ( text == null ) {
      throw new NullPointerException( "Text must not be null" );
    }
    if ( x < 0 ) {
      throw new IllegalArgumentException();
    }

    if ( y < 0 ) {
      throw new IllegalArgumentException();
    }

    if ( w < 1 ) {
      throw new IllegalArgumentException( "PlaintextDataChunk: Width is empty or negative. " + w );
    }
    if ( w > text.length() ) {
      throw new IllegalArgumentException( "Size limit: " + w + " vs. " + text.length() );
    }

    this.underline = underline;
    this.strikethrough = strikethrough;
    this.x = x;
    this.y = y;
    this.width = w;
    this.text = text;
    this.font = font;
    this.bold = bold;
    this.italic = italic;
  }

  public boolean isUnderline() {
    return underline;
  }

  public boolean isStrikethrough() {
    return strikethrough;
  }

  /**
   * Gets the text stored in this chunk.
   *
   * @return the text
   */
  public String getText() {
    return text;
  }

  public boolean isBold() {
    return bold;
  }

  public boolean isItalic() {
    return italic;
  }

  public String getFont() {
    return font;
  }

  /**
   * The column of the text start.
   *
   * @return the column of the first character.
   */
  public int getX() {
    return x;
  }

  /**
   * Gets the row where to print the text.
   *
   * @return the row.
   */
  public int getY() {
    return y;
  }

  /**
   * Gets the width of the text, the number of character which should be printed.
   *
   * @return the number of printable characters.
   */
  public int getWidth() {
    return width;
  }
}
