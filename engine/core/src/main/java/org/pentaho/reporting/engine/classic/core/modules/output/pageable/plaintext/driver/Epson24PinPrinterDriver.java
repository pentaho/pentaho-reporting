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

public class Epson24PinPrinterDriver extends AbstractEpsonPrinterDriver {
  private static final String SPECIFICATION_RESOURCE =
      "/org/pentaho/reporting/engine/classic/core/modules/output/pageable/plaintext/driver/epson-24pin-printer"
          + "-specifications.properties";
  public static final String EPSON_24PIN_PRINTER_TYPE =
      "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.epson.24PinPrinterType";

  private static final String N_360TH_LINE_SPACING = "Epson24pin.n360inch-linespacing";
  private static final String SELECT_LINE_SCORE = "Epson24pin.select-line-score";

  private static PrinterSpecificationManager printerSpecificationManager;

  public Epson24PinPrinterDriver( final OutputStream out, final float charsPerInch, final float linesPerInch,
      final String printerModel ) {
    super( out, charsPerInch, linesPerInch, printerModel );
  }

  protected void sendDefineLineSpacing( final float lineHeightInPoints ) throws IOException {
    final OutputStream outputStream = getOut();
    if ( getPrinterSpecification().isFeatureAvailable( Epson24PinPrinterDriver.N_360TH_LINE_SPACING ) ) {
      // Printers not supporting this command:
      // ActionPrinter L-1000, ActionPrinter 3000, LQ-200, LQ-500
      //
      // Set the line spacing with a resolution of 1/360th of an inch
      final int spacePar = (int) ( lineHeightInPoints * 5 );
      outputStream.write( 0x1b ); // ESC
      outputStream.write( 0x2b ); // +
      outputStream.write( spacePar );
    } else {
      // Set the line spacing with a resolution of 1/180th of an inch
      final int spacePar = (int) ( lineHeightInPoints * 2.5 ); // 1/180
      outputStream.write( 0x1b ); // ESC
      outputStream.write( 0x33 ); // 3
      outputStream.write( spacePar );
    }
  }

  protected void sendFontStyle( final boolean bold, final boolean italic, final boolean underline,
      final boolean strikeTrough ) throws IOException {
    final OutputStream out = getOut();
    final DriverState driverState = getDriverState();

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
        out.write( 0x1b ); // ESC
        out.write( 0x35 ); // 5
      }
    } else {
      if ( italic == true ) {
        // enable italic
        out.write( 0x1b ); // ESC
        out.write( 0x34 ); // 4
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

    // Printers not supporting this command:
    // ActionPrinter L-1000, LQ-400, LQ-500, LQ-2550
    //
    if ( getPrinterSpecification().isFeatureAvailable( Epson24PinPrinterDriver.SELECT_LINE_SCORE ) ) {
      if ( driverState.isStrikethrough() ) {
        if ( strikeTrough == false ) {
          // disable underline
          out.write( 0x1b ); // ESC
          out.write( 0x28 ); // (
          out.write( 0x2D ); // -
          out.write( 0x03 ); // const: 3
          out.write( 0x00 ); // const: 0
          out.write( 0x01 ); // const: 1
          out.write( 0x02 ); // select strikethrough
          out.write( 0x00 ); // disable
        }
      } else {
        if ( strikeTrough == true ) {
          // enable underline
          out.write( 0x1b ); // ESC
          out.write( 0x28 ); // (
          out.write( 0x2D ); // -
          out.write( 0x03 ); // const: 3
          out.write( 0x00 ); // const: 0
          out.write( 0x01 ); // const: 1
          out.write( 0x02 ); // select strikethrough
          out.write( 0x01 ); // enable with single continuous line
        }
      }
      driverState.setStrikethrough( strikeTrough );
    } else {
      driverState.setStrikethrough( false );
    }
  }

  protected PrinterSpecificationManager getPrinterSpecificationManager() {
    return Epson24PinPrinterDriver.loadSpecificationManager();
  }

  public static synchronized PrinterSpecificationManager loadSpecificationManager() {
    if ( Epson24PinPrinterDriver.printerSpecificationManager == null ) {
      Epson24PinPrinterDriver.printerSpecificationManager = new PrinterSpecificationManager();
      Epson24PinPrinterDriver.printerSpecificationManager.load( Epson24PinPrinterDriver.SPECIFICATION_RESOURCE );
    }
    return Epson24PinPrinterDriver.printerSpecificationManager;
  }

  public static String getDefaultPrinter() {
    return ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
        Epson24PinPrinterDriver.EPSON_24PIN_PRINTER_TYPE, "Generic 24-Pin printer" );
  }
}
