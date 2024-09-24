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
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper.PrinterEncoding;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper.PrinterSpecification;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

import java.awt.print.Paper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class IBMCompatiblePrinterDriver implements PrinterDriver {
  public static class GenericIBMPrinterSpecification implements PrinterSpecification {
    public GenericIBMPrinterSpecification() {
    }

    public String getDisplayName() {
      return getName();
    }

    /**
     * Returns the encoding definition for the given java encoding.
     *
     * @param encoding
     *          the java encoding that should be mapped into a printer specific encoding.
     * @return the printer specific encoding.
     * @throws IllegalArgumentException
     *           if the given encoding is not supported.
     */
    public PrinterEncoding getEncoding( final String encoding ) {
      try {
        return new PrinterEncoding( encoding, encoding, encoding,
            IBMCompatiblePrinterDriver.GenericIBMPrinterSpecification.translateCodePage( encoding ) );
      } catch ( UnsupportedEncodingException e ) {
        throw new IllegalArgumentException( "The given encoding is not supported." );
      }
    }

    /**
     * Translates the given Codepage String into a IBM Byte Code. The encoding string must be in the format CpXXXX where
     * XXXX is the number of the codepage.
     * <p/>
     *
     * @param cp
     *          the code page
     * @return the epson byte code.
     * @throws java.io.UnsupportedEncodingException
     *           if the encoding is not supported.
     */
    private static byte[] translateCodePage( final String cp ) throws UnsupportedEncodingException {
      // Mapping Rule:
      // n = NumberofCodePage + (10000 if codepage contains a character (Cp437G))
      if ( StringUtils.startsWithIgnoreCase( cp, "cp" ) ) {
        // check the supplied encoding ...
        // only Cp- encodings are supported ...
        if ( EncodingRegistry.getInstance().isSupportedEncoding( cp ) == false ) {
          throw new UnsupportedEncodingException( "The encoding " + cp + "is not valid" );
        }

        final String encodingName = cp.substring( 2 );
        try {
          int i;
          if ( Character.isDigit( encodingName.charAt( encodingName.length() - 1 ) ) == false ) {
            i = Integer.parseInt( encodingName.substring( 0, encodingName.length() - 1 ) );
            i += 10000;
          } else {
            i = Integer.parseInt( encodingName );
          }
          final byte[] retval = new byte[2];
          retval[0] = (byte) ( i >> 8 );
          retval[1] = (byte) ( i & 0xff );
          return retval;
        } catch ( Exception e ) {
          throw new UnsupportedEncodingException( "The encoding " + cp + "is not valid" );
        }
      }
      throw new UnsupportedEncodingException( "The encoding " + cp + " is no codepage encoding" );
    }

    /**
     * Returns the name of the encoding mapping. This is usually the same as the printer model name.
     *
     * @return the printer model.
     */
    public String getName() {
      return "Generic IBM Printer Specification";
    }

    /**
     * Checks whether the given Java-encoding is supported.
     *
     * @param encoding
     *          the java encoding that should be mapped into a printer specific encoding.
     * @return true, if there is a mapping, false otherwise.
     */
    public boolean isEncodingSupported( final String encoding ) {
      try {
        IBMCompatiblePrinterDriver.GenericIBMPrinterSpecification.translateCodePage( encoding );
        return true;
      } catch ( UnsupportedEncodingException use ) {
        return false;
      }
    }

    /**
     * Returns true, if a given operation is supported, false otherwise.
     *
     * @param operationName
     *          the operation, that should be performed
     * @return true, if the printer will be able to perform that operation, false otherwise.
     */
    public boolean isFeatureAvailable( final String operationName ) {
      // is not used yet.
      return true;
    }
  }

  private static class DriverState {
    private boolean bold;
    private boolean underline;
    private boolean italic;

    private byte font;
    private int manualLeftBorder;

    protected DriverState() {
    }

    public boolean isBold() {
      return bold;
    }

    public void setBold( final boolean bold ) {
      this.bold = bold;
    }

    public boolean isItalic() {
      return italic;
    }

    public void setItalic( final boolean italic ) {
      this.italic = italic;
    }

    public boolean isUnderline() {
      return underline;
    }

    public void setUnderline( final boolean underline ) {
      this.underline = underline;
    }

    public byte getFont() {
      return font;
    }

    public void setFont( final byte font ) {
      this.font = font;
    }

    public int getManualLeftBorder() {
      return manualLeftBorder;
    }

    public void setManualLeftBorder( final int manualLeftBorder ) {
      this.manualLeftBorder = manualLeftBorder;
    }
  }

  public static final int QUALITY_UNDEFINED = -1;
  public static final int QUALITY_FAST_DRAFT = 0;
  public static final int QUALITY_DRAFT = 0x40;
  public static final int QUALITY_LETTER = 0x80;
  public static final int QUALITY_ENHANCED_LETTER = 0xC0;
  public static final int QUALITY_DEFAULT = 0xFF;

  private OutputStream out;
  private float charsPerInch;
  private float linesPerInch;
  private byte[] endOfPage;

  private boolean autoLF;
  private int printQuality;
  private PrinterSpecification printerSpecification;
  private EncodingUtilities encodingUtilities;
  private DefaultFontMapper fontMapper;
  private IBMCompatiblePrinterDriver.DriverState driverState;
  private boolean firstPage;
  private String encoding;

  public IBMCompatiblePrinterDriver( final OutputStream out, final float charsPerInch, final float linesPerInch ) {
    this.out = out;
    this.charsPerInch = charsPerInch;
    this.linesPerInch = linesPerInch;
    this.endOfPage = new byte[] { (byte) PrinterDriverCommands.FORM_FEED };
    this.printerSpecification = new IBMCompatiblePrinterDriver.GenericIBMPrinterSpecification();
    this.fontMapper = new DefaultFontMapper();
    this.fontMapper.setDefaultFont( PrinterDriverCommands.SELECT_FONT_FROM_MENU );
    this.driverState = new IBMCompatiblePrinterDriver.DriverState();
    this.firstPage = true;
  }

  public void setAutoLF( final boolean autoLF ) {
    this.autoLF = autoLF;
  }

  public boolean isAutoLF() {
    return autoLF;
  }

  public int getPrintQuality() {
    return printQuality;
  }

  public void setPrintQuality( final int printQuality ) {
    this.printQuality = printQuality;
  }

  /**
   * Ends a new line.
   *
   * @param overflow
   * @throws java.io.IOException
   *           if an IOError occures.
   */
  public void endLine( final boolean overflow ) throws IOException {
    if ( overflow == false ) {
      out.write( PrinterDriverCommands.CARRIAGE_RETURN );
      if ( autoLF == false ) {
        out.write( PrinterDriverCommands.LINE_FEED );
      }
    }
  }

  /**
   * Ends the current page. Should print empty lines or an FORM_FEED command.
   *
   * @param overflow
   * @throws java.io.IOException
   *           if there was an IOError while writing the command
   */
  public void endPage( final boolean overflow ) throws IOException {
    if ( overflow == false ) {
      printRaw( endOfPage );
    }
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
    final String fd = chunk.getFont();
    sendDefineFont( fontMapper.getPrinterFont( fd ) );
    sendFontStyle( chunk.isBold(), chunk.isItalic(), chunk.isUnderline() );
    getEncodingUtilities( encoding ).writeEncodedText( text, out );
  }

  /**
   * Prints an empty chunk. This is called for all undefined chunk-cells. The last defined font is used to print that
   * empty text.
   *
   * @throws java.io.IOException
   *           if an IOError occured.
   */
  public void printEmptyChunk( final int count ) throws IOException {
    sendFontStyle( driverState.isBold(), driverState.isItalic(), false );
    for ( int i = 0; i < count; i++ ) {
      out.write( PrinterDriverCommands.SPACE );
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
    final int manualLeftBorder = driverState.getManualLeftBorder();
    for ( int i = 0; i < manualLeftBorder; i++ ) {
      out.write( PrinterDriverCommands.SPACE );
    }
  }

  /**
   * Resets the printer and starts a new page. Prints the top border lines (if necessary).
   *
   * @throws java.io.IOException
   *           if there was an IOError while writing the command
   */
  public void startPage( final Paper paper, final String encoding ) throws IOException {
    this.encoding = encoding;
    final float charWidthPoints = 72.0f / getCharactersPerInch();
    final float lineHeightPoints = 72.0f / getLinesPerInch();

    if ( firstPage ) {
      // update the autoLF setting
      sendAutoLF( isAutoLF() );
      sendDefinePrintQuality( getPrintQuality() );
      sendDefineCharacterWidth( getCharactersPerInch() );
      firstPage = false;
    }

    // set the line spacing ..
    sendLineSpacing( (int) lineHeightPoints );

    // define the page size ..
    // we redefine it for every page, as we do not assume that the page sizes
    // will be the same for the whole report.
    final int lines = (int) ( ( paper.getHeight() / 72.0f ) * getLinesPerInch() );
    sendDefinePageLengthInLines( lines );

    final PageFormatFactory fact = PageFormatFactory.getInstance();
    final int borderLeft = (int) ( fact.getLeftBorder( paper ) / charWidthPoints );
    final int borderRight = (int) ( fact.getRightBorder( paper ) / charWidthPoints );

    final int borderTop = (int) ( fact.getTopBorder( paper ) / lineHeightPoints );
    sendDefineHorizontalBorders( borderLeft, borderRight );

    // print the top margin ..
    for ( int i = 0; i < borderTop; i++ ) {
      startLine();
      endLine( false );
    }

  }

  /**
   * Defines the line spacing for the printer.
   *
   * @param lineHeight
   *          the height of a single line in points (1/72 inch).
   * @throws java.io.IOException
   *           if an IOException occured while updating the printer state.
   */
  public void sendLineSpacing( final int lineHeight ) throws IOException {
    out.write( 0x1b );
    out.write( 0x41 );
    out.write( lineHeight );
    out.write( 0x1b );
    out.write( 0x32 );
  }

  private void sendDefineHorizontalBorders( final int left, final int right ) throws IOException {
    out.write( 0x1b );
    out.write( 0x58 );
    out.write( left );
    out.write( right );
  }

  private void sendDefinePageLengthInLines( final int lines ) throws IOException {
    out.write( 0x1b );
    out.write( 0x43 );
    out.write( lines );
  }

  private void sendDefinePrintQuality( final int quality ) throws IOException {
    out.write( 0x1b );
    out.write( 0x5b );
    out.write( 0x64 );
    out.write( 0x01 );
    out.write( 0x00 );
    out.write( quality );
  }

  /**
   * Defines the font style for the printed text. The IBM-CommandSet does not support strike-through.
   *
   * @param bold
   *          true, if the text should be printed in bold mode.
   * @param italic
   *          true, if the text should be italic, false otherwise
   * @param underline
   *          true, if the text should be underlined, false otherwise
   * @throws java.io.IOException
   *           if there was an IOError while writing the command
   */
  private void sendFontStyle( final boolean bold, final boolean italic, final boolean underline ) throws IOException {
    if ( driverState.isBold() ) {
      if ( bold == false ) {
        // disable bold
        out.write( 0x1b ); // ESC
        out.write( 0x46 ); // F
      }
    } else {
      if ( bold == true ) {
        // enable bold
        out.write( 0x1b ); // ESC
        out.write( 0x45 ); // E
      }
    }

    if ( driverState.isItalic() ) {
      if ( italic == false ) {
        // disable italic
        out.write( 0x1b );
        out.write( 0x25 );
        out.write( 0x48 );
      }
    } else {
      if ( italic == true ) {
        // enable italic
        out.write( 0x1b );
        out.write( 0x25 );
        out.write( 0x47 );
      }
    }

    if ( driverState.isUnderline() ) {
      if ( underline == false ) {
        // disable underline
        out.write( 0x1b ); // ESC
        out.write( 0x2d ); // -
        out.write( 0x00 ); // 0
      }
    } else {
      if ( underline == true ) {
        // enable underline
        out.write( 0x1b ); // ESC
        out.write( 0x2d ); // -
        out.write( 0x01 ); // 1
      }
    }

    driverState.setBold( bold );
    driverState.setItalic( italic );
    driverState.setUnderline( underline );

  }

  private float sendDefineCharacterWidth( final float charsPerInch ) throws IOException {
    if ( charsPerInch <= 10 ) {
      out.write( 0x12 );
      return 10;
    } else if ( charsPerInch <= 12 ) {
      out.write( 0x1b );
      out.write( 0x3a );
      return 12;
    } else if ( charsPerInch <= 15 ) {
      out.write( 0x1b );
      out.write( 0x67 );
      return 15;
    } else if ( charsPerInch <= 17.4 ) {
      out.write( 0x0f );
      return 17.4f;
    } else {
      out.write( 0x1b );
      out.write( 0x0f );
      return 20;
    }
  }

  private void sendDefineCodepage( final String codePage ) throws IOException {
    final PrinterEncoding spec = getPrinterSpecification().getEncoding( codePage );
    final byte[] cp = spec.getCode();
    out.write( 0x1b ); // ESC
    out.write( 0x5b ); // [
    out.write( 0x54 ); // T
    out.write( 0x04 ); // 0x04 (according to LexMark Manual P.30)
    out.write( 0x00 ); // const.
    out.write( 0x00 ); // const.
    out.write( 0x00 ); // const.
    out.write( cp ); // codepage as 2 byte sequence
  }

  private PrinterSpecification getPrinterSpecification() {
    return printerSpecification;
  }

  private void sendAutoLF( final boolean autoLF ) throws IOException {
    if ( autoLF == false ) {
      out.write( 0x1b );
      out.write( 0x35 );
      out.write( 0x30 );
    } else {
      out.write( 0x1b );
      out.write( 0x35 );
      out.write( 0x31 );
    }
  }

  private void sendDefineFont( final byte b ) throws IOException {
    if ( driverState.getFont() != b ) {
      out.write( 0x1b );
      out.write( 0x6b );
      out.write( b );

      driverState.setFont( b );
    }
  }

  protected EncodingUtilities getEncodingUtilities( final String encoding ) throws IOException {
    if ( encodingUtilities != null && encodingUtilities.getEncoding().equals( encoding ) ) {
      return encodingUtilities;
    }

    encodingUtilities = new EncodingUtilities( encoding );
    sendDefineCodepage( encoding );
    return encodingUtilities;
  }
}
