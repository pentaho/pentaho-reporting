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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper.PrinterSpecificationManager;

import java.io.IOException;
import java.io.OutputStream;

public class Epson9PinPrinterDriver extends AbstractEpsonPrinterDriver {
  private static final byte TWELVECPI = 0x01;
  private static final byte CONDENSED = 0x04;
  private static final byte BOLD = 0x08;
  private static final byte ITALICS = 0x40;
  private static final byte UNDERLINE = (byte) 0x80;
  private int masterselect;
  private static PrinterSpecificationManager printerSpecificationManager;
  private static final String SPECIFICATION_RESOURCE =
      "/org/pentaho/reporting/engine/classic/core/modules/output/pageable/plaintext/driver/epson-9pin-printer"
          + "-specifications.properties";
  public static final String EPSON_9PIN_PRINTER_TYPE =
      "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.epson.9PinPrinterType";

  public Epson9PinPrinterDriver( final OutputStream out, final float charsPerInch, final float linesPerInch,
      final String printerModel ) {
    super( out, charsPerInch, linesPerInch, printerModel );
  }

  protected void sendDefineLineSpacing( final float lineHeightInPoints ) throws IOException {
    // All printers support that command.
    final int spacePar = (int) ( lineHeightInPoints * 3 ); // 1/216
    final OutputStream outputStream = getOut();
    outputStream.write( 0x1b ); // ESC
    outputStream.write( 0x33 ); // 3
    outputStream.write( spacePar );
  }

  protected void sendFontStyle( final boolean bold, final boolean italic, final boolean underline,
      final boolean strikeTrough ) throws IOException {
    final OutputStream out = getOut();
    final DriverState driverState = getDriverState();
    final byte[] bytes = new byte[8];
    int byteindex = 0;

    if ( driverState.isBold() ) {
      if ( bold == false ) {
        // disable bold
        masterselect &= ~Epson9PinPrinterDriver.BOLD;
        bytes[byteindex] = 0x1b;
        byteindex++;
        bytes[byteindex] = 0x46;
        byteindex++;
      }
    } else {
      if ( bold == true ) {
        // enable bold
        masterselect |= Epson9PinPrinterDriver.BOLD;
        bytes[byteindex] = 0x1b;
        byteindex++;
        bytes[byteindex] = 0x45;
        byteindex++;
      }
    }

    if ( driverState.isItalic() ) {
      if ( italic == false ) {
        // disable italic
        masterselect &= ~Epson9PinPrinterDriver.ITALICS;
        bytes[byteindex] = 0x1b;
        byteindex++;
        bytes[byteindex] = 0x35;
        byteindex++;
      }
    } else {
      if ( italic == true ) {
        // enable italic
        masterselect |= Epson9PinPrinterDriver.ITALICS;
        bytes[byteindex] = 0x1b;
        byteindex++;
        bytes[byteindex] = 0x34;
        byteindex++;
      }
    }

    if ( driverState.isUnderline() ) {
      if ( underline == false ) {
        // disable underline
        masterselect &= ~Epson9PinPrinterDriver.UNDERLINE;
        bytes[byteindex] = 0x1b;
        byteindex++;
        bytes[byteindex] = 0x2d;
        byteindex++;
        bytes[byteindex] = 0x00;
        byteindex++;
      }
    } else {
      if ( underline == true ) {
        // enable underline
        masterselect |= Epson9PinPrinterDriver.UNDERLINE;
        bytes[byteindex] = 0x1b;
        byteindex++;
        bytes[byteindex] = 0x2d;
        byteindex++;
        bytes[byteindex] = 0x01;
        byteindex++;
      }
    }
    final boolean useMasterSelect =
        ClassicEngineBoot.getInstance().getExtendedConfig().getBoolProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.UseEpsonMasterSelect" );

    if ( useMasterSelect ) {
      out.write( 0x1b ); // disable condensed printing
      out.write( 0x21 );
      out.write( (byte) masterselect );
    } else {
      for ( int i = 0; i < byteindex; i++ ) {
        out.write( bytes[i] );
      }
    }
    driverState.setBold( bold );
    driverState.setItalic( italic );
    driverState.setUnderline( underline );
    driverState.setStrikethrough( false );
  }

  protected void sendDefineCharacterWidth( final float charactersPerInch ) throws IOException {
    final byte[] bytes = new byte[4];
    int byteindex = 0;
    boolean useMasterSelect =
        ClassicEngineBoot.getInstance().getExtendedConfig().getBoolProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.UseEpsonMasterSelect" );
    if ( charactersPerInch == PrinterDriverCommands.CPI_10 ) {
      masterselect &= ~Epson9PinPrinterDriver.TWELVECPI;
      bytes[byteindex++] = 0x12; // disable condensed printing
      bytes[byteindex++] = 0x1b;
      bytes[byteindex++] = 0x50; // select 10 CPI
    } else if ( charactersPerInch == PrinterDriverCommands.CPI_12 ) {
      masterselect |= Epson9PinPrinterDriver.TWELVECPI;
      bytes[byteindex++] = 0x12; // disable condensed printing
      bytes[byteindex++] = 0x1b;
      bytes[byteindex++] = 0x4d; // select 12 CPI
    } else if ( charactersPerInch == PrinterDriverCommands.CPI_15 ) {
      // All ESC/P2 and 24Pin ESC/P printers support that mode
      // Additionally, the 9Pin printer models FX-2170 and DFX-5000+
      // support that character width.
      bytes[byteindex++] = 0x12; // disable condensed printing
      bytes[byteindex++] = 0x1b;
      bytes[byteindex++] = 0x67; // select 15 CPI
      useMasterSelect = false;
    } else if ( charactersPerInch == PrinterDriverCommands.CPI_17 ) {
      masterselect |= Epson9PinPrinterDriver.CONDENSED;
      masterselect &= ~Epson9PinPrinterDriver.TWELVECPI;
      bytes[byteindex++] = 0x0f; // enable condensed printing
      bytes[byteindex++] = 0x1b;
      bytes[byteindex++] = 0x50; // select 10 CPI (-> 17.14 cpi because of condensed printing)
    } else if ( charactersPerInch == PrinterDriverCommands.CPI_20 ) {
      masterselect |= Epson9PinPrinterDriver.CONDENSED;
      masterselect |= Epson9PinPrinterDriver.TWELVECPI;
      bytes[byteindex++] = 0x0f; // enable condensed printing
      bytes[byteindex++] = 0x1b;
      bytes[byteindex++] = 0x4d; // select 12 CPI (-> 20 cpi because of condensed printing)
    } else {
      throw new IllegalArgumentException( "The given character width is invalid" );
    }

    final OutputStream outputStream = getOut();
    if ( useMasterSelect ) {
      outputStream.write( 0x1b );
      outputStream.write( 0x21 );
      outputStream.write( (byte) masterselect );
    } else {
      for ( int i = 0; i < byteindex; i++ ) {
        outputStream.write( bytes[i] );
      }
    }
  }

  protected PrinterSpecificationManager getPrinterSpecificationManager() {
    return Epson9PinPrinterDriver.loadSpecificationManager();
  }

  public static synchronized PrinterSpecificationManager loadSpecificationManager() {
    if ( Epson9PinPrinterDriver.printerSpecificationManager == null ) {
      Epson9PinPrinterDriver.printerSpecificationManager = new PrinterSpecificationManager();
      Epson9PinPrinterDriver.printerSpecificationManager.load( Epson9PinPrinterDriver.SPECIFICATION_RESOURCE );
    }
    return Epson9PinPrinterDriver.printerSpecificationManager;
  }

  public static String getDefaultPrinter() {
    return ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
        Epson9PinPrinterDriver.EPSON_9PIN_PRINTER_TYPE, "Generic 9-Pin printer" );
  }
}
