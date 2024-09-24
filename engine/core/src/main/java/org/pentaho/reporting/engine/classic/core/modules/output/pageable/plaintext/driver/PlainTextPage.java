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

import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

import java.awt.print.Paper;
import java.io.IOException;

/**
 * The plain text page is used to buffer a complete page and to write the buffered data when the page is closed.
 *
 * @author Thomas Morgner
 */
public class PlainTextPage {

  /**
   * the page buffer is used to store all TextDataChunks.
   */
  private PlaintextDataChunk[][] pageBuffer;

  /**
   * The command-set that is used to finally print the content.
   */
  private PrinterDriver driver;

  /**
   * The width of the page in characters.
   */
  private int width;

  /**
   * the height of the page in lines.
   */
  private int height;

  private Paper paper;

  private String defaultEncoding;

  /**
   * Creates a new PlainTextPage with the given dimensions and the specified PrinterCommandSet.
   *
   * @param driver
   *          the command-set for printing and formatting the text.
   */
  public PlainTextPage( final Paper pageFormat, final PrinterDriver driver, final String defaultEncoding ) {
    if ( driver == null ) {
      throw new NullPointerException( "PrinterCommandSet must be defined." );
    }
    if ( pageFormat == null ) {
      throw new NullPointerException( "PageFormat must be defined." );
    }
    if ( defaultEncoding == null ) {
      throw new NullPointerException( "DefaultEncoding must be defined." );
    }

    final float characterWidthInPoint = ( 72.0f / driver.getCharactersPerInch() );
    final float characterHeightInPoint = ( 72.0f / driver.getLinesPerInch() );

    final int currentPageHeight =
        PlainTextPage.correctedDivisionFloor( pageFormat.getImageableHeight(), characterHeightInPoint );
    final int currentPageWidth =
        PlainTextPage.correctedDivisionFloor( pageFormat.getImageableWidth(), characterWidthInPoint );

    // Log.debug("Created page with " + currentPageWidth + ", " + currentPageHeight);
    pageBuffer = new PlaintextDataChunk[currentPageWidth][currentPageHeight];
    width = currentPageWidth;
    height = currentPageHeight;
    paper = pageFormat;
    this.driver = driver;
    this.defaultEncoding = defaultEncoding;
  }

  /**
   * Fixes some floating point errors when calculating positions.
   *
   * @param c
   *          the divisor
   * @param d
   *          the dividend
   * @return the corrected division result.
   */
  public static int correctedDivisionFloor( double c, double d ) {
    c = Math.round( c * 100.0f );
    d = Math.round( d * 100.0f );
    return (int) Math.floor( c / d );
  }

  /**
   * Returns the page width in characters.
   *
   * @return the page width.
   */
  public int getWidth() {
    return width;
  }

  /**
   * Returns the page height in lines.
   *
   * @return the page height.
   */
  public int getHeight() {
    return height;
  }

  /**
   * Adds a new text chunk to this PlainTextPage. A chunk consists of a single line of text.
   *
   * @param x
   *          the column of the first character of the text
   * @param y
   *          the row where to print the text
   * @param w
   *          the number of characters to print.
   * @param text
   *          the text that should be printed.
   * @param format
   *          the font definition used to format the text.
   */
  public void addTextChunk( final int x, final int y, final int w, final String text, final StyleSheet format ) {
    if ( text.length() == 0 ) {
      return;
    }
    if ( x < 0 ) {
      throw new IllegalArgumentException( "X < 0: " + x );
    }
    if ( y < 0 ) {
      throw new IllegalArgumentException( "y < 0: " + y );
    }
    if ( w < 0 ) {
      throw new IllegalArgumentException( "w < 0 " + w );
    }
    if ( x + w > width ) {
      throw new IllegalArgumentException( "X+W [" + ( x + w ) + "] > bufferWidth [" + width + ']' );
    }
    if ( y >= height ) {
      throw new IllegalArgumentException( "Y > bufferHeight: " + text + " y=" + y + " h=" + height );
    }
    final String font = (String) format.getStyleProperty( TextStyleKeys.FONT );
    final boolean bold = format.getBooleanStyleProperty( TextStyleKeys.BOLD );
    final boolean italic = format.getBooleanStyleProperty( TextStyleKeys.ITALIC );
    final boolean underline = format.getBooleanStyleProperty( TextStyleKeys.UNDERLINED );
    final boolean strikethrough = format.getBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH );

    final PlaintextDataChunk chunk =
        new PlaintextDataChunk( text, font, bold, italic, underline, strikethrough, x, y, w );

    // TODO: Why are we storing a chunk 'x' number of times where 'x' the width of the chunk
    for ( int i = 0; i < w; i++ ) {
      if ( pageBuffer[x + i][y] == null ) {
        pageBuffer[x + i][y] = chunk;
      }
    }
  }

  /**
   * returns the chunk stored at the given position or null, if no chunk was stored there.
   *
   * @param x
   *          the column
   * @param y
   *          the line
   * @return the text chunk or null.
   */
  private PlaintextDataChunk getChunk( final int x, final int y ) {
    return pageBuffer[x][y];
  }

  /**
   * Writes the contents of the page using the printer command set.
   *
   * @throws java.io.IOException
   *           if an I/O error occured while writing the page.
   */
  public void writePage() throws IOException {
    driver.startPage( paper, defaultEncoding );
    for ( int y = 0; y < height; y++ ) {
      driver.startLine();
      int emptyChunkCount = 0;
      boolean overflow = false;

      for ( int x = 0; x < width; x++ ) {
        final PlaintextDataChunk chunk = getChunk( x, y );
        if ( chunk == null ) {
          emptyChunkCount += 1;
        } else if ( chunk.getX() == x ) {
          if ( emptyChunkCount != 0 ) {
            driver.printEmptyChunk( emptyChunkCount );
            emptyChunkCount = 0;
          }

          // Log.debug ("Print Chunk At " + x);
          driver.printChunk( chunk );
          x += ( chunk.getWidth() - 1 );
          // we reached the end of the line ...
          if ( x == ( width - 1 ) ) {
            overflow = true;
          }
        }
      }

      // end the page on the last line. Note: overflow is ignored when ending page or line.
      if ( y == ( height - 1 ) ) {
        driver.endPage( overflow );
      } else {
        driver.endLine( overflow );
      }
    }
    driver.flush();
  }
}
