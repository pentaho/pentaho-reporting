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

import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper.EncodingUtilities;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;

import java.awt.print.Paper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * This printer driver will ignore all TextChunk encodings, as it makes no sense to have more than one encoding type in
 * a plain text file.
 */
public class TextFilePrinterDriver implements PrinterDriver {
  private OutputStream out;
  private char[] endOfLine;
  private char[] endOfPage;
  private float charsPerInch;
  private float linesPerInch;
  private EncodingUtilities encodingUtilities;
  private String defaultEncoding;
  private boolean firstPage;

  private int borderLeft;

  public TextFilePrinterDriver( final OutputStream out, final float charsPerInch, final float linesPerInch ) {
    this( out, charsPerInch, linesPerInch, false );
  }

  public TextFilePrinterDriver( final OutputStream out, final float charsPerInch, final float linesPerInch,
      final boolean unixEndOfLine ) {
    this.out = out;
    this.charsPerInch = charsPerInch;
    this.linesPerInch = linesPerInch;
    if ( unixEndOfLine == false ) {
      this.endOfLine = new char[] { PrinterDriverCommands.CARRIAGE_RETURN, PrinterDriverCommands.LINE_FEED };
      this.endOfPage =
          new char[] { PrinterDriverCommands.CARRIAGE_RETURN, PrinterDriverCommands.LINE_FEED,
            PrinterDriverCommands.FORM_FEED };
    } else {
      this.endOfLine = new char[] { PrinterDriverCommands.LINE_FEED };
      this.endOfPage = new char[] { PrinterDriverCommands.LINE_FEED, PrinterDriverCommands.FORM_FEED };
    }
    this.firstPage = true;
  }

  /**
   * Ends a new line.
   *
   * @param overflow
   * @throws java.io.IOException
   *           if an IOError occures.
   */
  public void endLine( final boolean overflow ) throws IOException {
    getEncodingUtilities( defaultEncoding ).writeEncodedText( endOfLine, out );
  }

  /**
   * Ends the current page. Should print empty lines or an FORM_FEED command.
   *
   * @param overflow
   * @throws java.io.IOException
   *           if there was an IOError while writing the command
   */
  public void endPage( final boolean overflow ) throws IOException {
    getEncodingUtilities( defaultEncoding ).writeEncodedText( endOfPage, out );
  }

  /**
   * Flushes the output stream.
   *
   * @throws java.io.IOException
   *           if an IOError occured.
   */
  public void flush() throws IOException {
    out.flush();
  }

  /**
   * Gets the default character width in CPI.
   *
   * @return the default character width in CPI.
   */
  public float getCharactersPerInch() {
    return charsPerInch;
  }

  /**
   * Gets the default line height.
   *
   * @return the default line height.
   */
  public float getLinesPerInch() {
    return linesPerInch;
  }

  /**
   * Prints a single text chunk at the given position on the current line. The chunk should not be printed, if an
   * previous chunk overlays this chunk.
   *
   * @param chunk
   *          the chunk that should be written
   * @throws java.io.IOException
   *           if an IO error occured.
   */
  public void printChunk( final PlaintextDataChunk chunk ) throws IOException {
    final String text = chunk.getText().substring( 0, chunk.getWidth() );
    getEncodingUtilities( defaultEncoding ).writeEncodedText( text, out );
  }

  /**
   * Prints an empty chunk. This is called for all undefined chunk-cells.
   *
   * @throws java.io.IOException
   *           if an IOError occured.
   */
  public void printEmptyChunk( final int count ) throws IOException {
    final EncodingUtilities encodingUtilities = getEncodingUtilities( defaultEncoding );
    for ( int i = 0; i < count; i++ ) {
      out.write( encodingUtilities.getSpace() );
    }
  }

  /**
   * Prints some raw content. This content is not processed in any way, so be very carefull.
   *
   * @param raw
   *          the content that should be printed.
   */
  public void printRaw( final byte[] raw ) throws IOException {
    out.write( raw );
  }

  /**
   * Starts a new line.
   *
   * @throws java.io.IOException
   *           if an IOError occures.
   */
  public void startLine() throws IOException {
    printEmptyChunk( borderLeft );
  }

  /**
   * Resets the printer and starts a new page. Prints the top border lines (if necessary).
   *
   * @throws java.io.IOException
   *           if there was an IOError while writing the command
   */
  public void startPage( final Paper paper, final String encoding ) throws IOException {
    this.defaultEncoding = encoding;

    if ( firstPage ) {
      final EncodingUtilities encodingUtilities = getEncodingUtilities( encoding );
      out.write( encodingUtilities.getEncodingHeader() );
      firstPage = false;
    }

    final PageFormatFactory fact = PageFormatFactory.getInstance();
    final float charWidthPoints = 72.0f / getCharactersPerInch();
    borderLeft = (int) ( fact.getLeftBorder( paper ) / charWidthPoints );

    final float lineHeightPoints = 72.0f / getLinesPerInch();
    final int borderTop = (int) ( fact.getTopBorder( paper ) / lineHeightPoints );

    for ( int i = 0; i < borderTop; i++ ) {
      startLine();
      endLine( false );
    }
  }

  protected EncodingUtilities getEncodingUtilities( final String encoding ) throws UnsupportedEncodingException {
    if ( encodingUtilities != null && encodingUtilities.getEncoding().equals( encoding ) ) {
      return encodingUtilities;
    }

    encodingUtilities = new EncodingUtilities( encoding );
    return encodingUtilities;
  }

  public char[] getEndOfLine() {
    return endOfLine;
  }

  public void setEndOfLine( final char[] endOfLine ) {
    if ( endOfLine == null ) {
      throw new NullPointerException();
    }
    this.endOfLine = (char[]) endOfLine.clone();
  }

  public char[] getEndOfPage() {
    return endOfPage;
  }

  public void setEndOfPage( final char[] endOfPage ) {
    if ( endOfPage == null ) {
      throw new NullPointerException();
    }
    this.endOfPage = (char[]) endOfPage.clone();
  }
}
