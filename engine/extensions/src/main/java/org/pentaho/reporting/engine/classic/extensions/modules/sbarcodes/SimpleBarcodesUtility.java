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

package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.linear.codabar.CodabarBarcode;
import net.sourceforge.barbecue.linear.code128.Code128Barcode;
import net.sourceforge.barbecue.linear.code39.Code39Barcode;
import net.sourceforge.barbecue.linear.ean.BooklandBarcode;
import net.sourceforge.barbecue.linear.ean.EAN13Barcode;
import net.sourceforge.barbecue.linear.ean.UCCEAN128Barcode;
import net.sourceforge.barbecue.linear.postnet.PostNetBarcode;
import net.sourceforge.barbecue.linear.twoOfFive.Int2of5Barcode;
import net.sourceforge.barbecue.linear.twoOfFive.Std2of5Barcode;
import net.sourceforge.barbecue.linear.upc.UPCABarcode;
import net.sourceforge.barbecue.twod.pdf417.PDF417Barcode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.EAN128;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrix;
import org.krysalis.barcode4j.impl.fourstate.RoyalMailCBC;
import org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMail;
import org.krysalis.barcode4j.impl.upcean.EAN8;
import org.krysalis.barcode4j.impl.upcean.UPCE;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * This utility shares with <code>SimpleBarcodesExpression</code> and <code>SimpleBarcodesType</code> the initial
 * barcode creation mainly based on the type which is given as a string.
 *
 * @author Cedric Pronzato
 */
public class SimpleBarcodesUtility {
  /**
   * Symbology accepting A-Z 0-9 and 8 special characters '-' '.' ' ' '*' '$' '/' '+' '%'.<br/>
   * Also known as 3of9, usd3, usd-3.<br/>
   * This barcode supports checksum property.
   */
  public static final String BARCODE_CODE39 = "code39";
  /**
   * Symbology accepting ASCII (128 characters).<br/>
   * Also known as 3of9ext, usd3ext, usd-3ext. <br/>
   * This barcode supports checksum property.
   */
  public static final String BARCODE_CODE39EXT = "code39ext";
  /**
   * Symbology accepting O-9 plus 6 additional characters '-' '$' '.' ':' '/' '+'.<br/>
   * Also known as code27, 2of7, usd4, nw7, monarch.
   */
  public static final String BARCODE_CODABAR = "codabar";
  /**
   * Symbology accepting ASCII (128 characters) in a reduced space.<br/>
   * It automaticaly switch between the diffrent code sets.
   *
   * @see {BARCODE_CODE128A} for code set A.
   * @see {BARCODE_CODE128B} for code set B.
   * @see {BARCODE_CODE128C} for code set C.
   */
  public static final String BARCODE_CODE128 = "code128";
  /**
   * Symbology accepting 0-9 A-Z ' ' '!' '"' '#' '$' '%' '&' ''' '(' ')' '*' '+' '-' ',' '.' '/' ';' ':' '<' '>' '=' '?'
   * '@' '[' ']' '^' '_' and a lot of control characters.
   */
  public static final String BARCODE_CODE128A = "code128a";
  /**
   * Symbology accepting ASCII (128 characters)
   */
  public static final String BARCODE_CODE128B = "code128b";
  /**
   * Symbology accepting numbers.
   */
  public static final String BARCODE_CODE128C = "code128c";
  /**
   * Symbology accepting 0-9 with a size of 12 characters. If the size is 11 a checksum will be automaticaly added.<br/>
   * Also known as ean-13.
   */
  public static final String BARCODE_EAN13 = "ean13";
  /**
   * Symbology accepting 0-9 with a size of 12 characters. If the size is 11 a checksum will be automaticaly added.<br/>
   * Also known as upc-a.
   */
  public static final String BARCODE_UPCA = "upca";
  /**
   * Symbology accepting 0-9 with a size of 10 characters. If the size is 9 a checksum will be automaticaly added?<br/>
   * Also known as bookland.
   */
  public static final String BARCODE_ISBN = "isbn";
  /**
   * Symbology accepting numbers.<br/>
   * This barcode supports checksum property.
   */
  public static final String BARCODE_UCCEAN128 = "uccean128";
  /**
   * Symbology accepting 0-9.<br/>
   * This barcode supports checksum property.<br/>
   * Also known as 2of5std.
   */
  public static final String BARCODE_2OF5 = "2of5";
  /**
   * Symbology accepting 0-9 in a reduced space in comparison of standard 2of5.<br/>
   * This barcode supports checksum property.<br/>
   * Also known as 2of5std.
   */
  public static final String BARCODE_2OF5INT = "2of5int";
  /**
   * Symbology accepting 0-9 with a size of 5 to 11 characters?<br/>
   */
  public static final String BARCODE_POSTNET = "postnet";
  /**
   * 2 dimensional symbology accepting 2700 characters long.<br/>
   * This barcode does not support any of the conventional barcode properties (barWidth, barHeight, checksum, showText,
   * ...).<br/>
   * Also known as pdf-417.
   */
  public static final String BARCODE_PDF417 = "pdf417";

  public static final String BARCODE_EAN8 = "ean8";
  public static final String BARCODE_EAN128 = "ean128";
  public static final String BARCODE_UPCE = "upce";
  public static final String BARCODE_DATAMATRIX = "datamatrix";
  public static final String BARCODE_ROYALMAIL = "royalmail";
  public static final String BARCODE_USPSINTELLIGENTMAIL = "uspsintelligentmail";

  private static final Log logger = LogFactory.getLog( SimpleBarcodesUtility.class );

  private SimpleBarcodesUtility() {
  }

  public static Barcode createBarcode( final String data, final String type, final boolean checksum ) {
    if ( data == null || type == null ) {
      throw new IllegalArgumentException( "Barcode type or data must not be null" );
    }

    if ( BARCODE_CODE39.equals( type ) ) {
      try {
        return new Code39Barcode( data, checksum, false );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong code39(ext) data supplied", e );
        return null;
      }
    }

    if ( BARCODE_CODE39EXT.equals( type ) ) {
      try {
        return new Code39Barcode( data, checksum, true );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong code39(ext) data supplied", e );
        return null;
      }
    }

    if ( BARCODE_CODABAR.equals( type ) ) {
      try {
        return new CodabarBarcode( data );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong codabar data supplied", e );
        return null;
      }
    }

    if ( BARCODE_EAN13.equals( type ) ) {
      try {
        return new EAN13Barcode( data );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong ean13 data supplied", e );
        return null;
      }
    }

    if ( BARCODE_UPCA.equals( type ) ) {
      try {
        return new UPCABarcode( data, false );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong upca data supplied", e );
        return null;
      }
    }

    if ( BARCODE_ISBN.equals( type ) ) {
      try {
        return new BooklandBarcode( data );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong isbn data supplied", e );
        return null;
      }
    }

    if ( BARCODE_CODE128.equals( type ) ) {
      try {
        return new Code128Barcode( data, Code128Barcode.O );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong code128 data supplied", e );
        return null;
      }
    }

    if ( BARCODE_CODE128A.equals( type ) ) {
      try {
        return new Code128Barcode( data, Code128Barcode.A );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong code128 data supplied", e );
        return null;
      }
    }

    if ( BARCODE_CODE128B.equals( type ) ) {
      try {
        return new Code128Barcode( data, Code128Barcode.B );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong code128 data supplied", e );
        return null;
      }
    }

    if ( BARCODE_CODE128C.equals( type ) ) {
      try {
        return new Code128Barcode( data, Code128Barcode.C );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong code128 data supplied", e );
        return null;
      }
    }

    if ( BARCODE_UCCEAN128.equals( type ) ) {
      try {
        return new UCCEAN128Barcode( data, checksum );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong uccean128 data supplied", e );
        return null;
      }
    }

    if ( BARCODE_2OF5.equals( type ) ) {
      try {
        return new Std2of5Barcode( data, checksum );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong std2of5 data supplied", e );
        return null;
      }
    }

    if ( BARCODE_2OF5INT.equals( type ) ) {
      try {
        return new Int2of5Barcode( data, checksum );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong int2of5 data supplied", e );
        return null;
      }
    }

    if ( BARCODE_POSTNET.equals( type ) ) {
      try {
        if ( checksum ) {
          logger.info( "Checkum property is not usable on barcode " + type );
        }
        return new PostNetBarcode( data );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong postnet data supplied", e );
        return null;
      }
    }

    if ( BARCODE_PDF417.equals( type ) ) {
      try {
        return new PDF417Barcode( data );
      } catch ( BarcodeException e ) {
        logger.error( "Wrong postnet data supplied", e );
        return null;
      }
    }

    logger.warn( "Unknown barcode type '" + type + "'." );
    return null;
  }

  public static BarcodeGenerator createBarcode4J( final String type, final boolean showText, final boolean checksum,
      final Number barHeight ) {
    if ( BARCODE_DATAMATRIX.equals( type ) ) {
      final DataMatrix dataMatrix = new DataMatrix();
      return dataMatrix;
    }

    if ( BARCODE_EAN8.equals( type ) ) {
      final EAN8 dataMatrix = new EAN8();
      if ( showText == false ) {
        dataMatrix.getUPCEANBean().setMsgPosition( HumanReadablePlacement.HRP_NONE );
      }
      if ( barHeight != null ) {
        dataMatrix.getUPCEANBean().setBarHeight( UnitConv.pt2mm( barHeight.doubleValue() ) );
      }
      dataMatrix.getUPCEANBean().setChecksumMode( checksum ? ChecksumMode.CP_AUTO : ChecksumMode.CP_IGNORE );
      return dataMatrix;
    }

    if ( BARCODE_EAN128.equals( type ) ) {
      final EAN128 dataMatrix = new EAN128();
      if ( showText == false ) {
        dataMatrix.getEAN128Bean().setMsgPosition( HumanReadablePlacement.HRP_NONE );
      }
      if ( barHeight != null ) {
        dataMatrix.getEAN128Bean().setBarHeight( UnitConv.pt2mm( barHeight.doubleValue() ) );
      }
      dataMatrix.getEAN128Bean().setChecksumMode( checksum ? ChecksumMode.CP_AUTO : ChecksumMode.CP_IGNORE );
      return dataMatrix;
    }

    if ( BARCODE_UPCE.equals( type ) ) {
      final UPCE dataMatrix = new UPCE();
      if ( showText == false ) {
        dataMatrix.getUPCEANBean().setMsgPosition( HumanReadablePlacement.HRP_NONE );
      }
      if ( barHeight != null ) {
        dataMatrix.getUPCEANBean().setBarHeight( UnitConv.pt2mm( barHeight.doubleValue() ) );
      }
      dataMatrix.getUPCEANBean().setChecksumMode( checksum ? ChecksumMode.CP_AUTO : ChecksumMode.CP_IGNORE );
      return dataMatrix;
    }

    if ( BARCODE_ROYALMAIL.equals( type ) ) {
      final RoyalMailCBC dataMatrix = new RoyalMailCBC();
      if ( showText == false ) {
        dataMatrix.getRoyalMailCBCBean().setMsgPosition( HumanReadablePlacement.HRP_NONE );
      }
      if ( barHeight != null ) {
        dataMatrix.getRoyalMailCBCBean().setBarHeight( UnitConv.pt2mm( barHeight.doubleValue() ) );
      }
      dataMatrix.getRoyalMailCBCBean().setChecksumMode( checksum ? ChecksumMode.CP_AUTO : ChecksumMode.CP_IGNORE );
      return dataMatrix;
    }

    if ( BARCODE_USPSINTELLIGENTMAIL.equals( type ) ) {
      final USPSIntelligentMail dataMatrix = new USPSIntelligentMail();
      if ( showText == false ) {
        dataMatrix.getUSPSIntelligentMailBean().setMsgPosition( HumanReadablePlacement.HRP_NONE );
      }
      if ( barHeight != null ) {
        dataMatrix.getUSPSIntelligentMailBean().setBarHeight( UnitConv.pt2mm( barHeight.doubleValue() ) );
      }
      dataMatrix.getUSPSIntelligentMailBean()
          .setChecksumMode( checksum ? ChecksumMode.CP_AUTO : ChecksumMode.CP_IGNORE );
      return dataMatrix;
    }
    return null;
  }

  public static String getBarcodeSampleData( final String type ) {
    if ( BARCODE_DATAMATRIX.equals( type ) ) {
      return "barcode";
    }

    if ( BARCODE_EAN8.equals( type ) ) {
      return "01234565";
    }
    if ( BARCODE_EAN13.equals( type ) ) {
      return "012345678912";
    }
    if ( BARCODE_ISBN.equals( type ) ) {
      return "0123456789";
    }

    if ( BARCODE_EAN128.equals( type ) ) {
      return "barcode";
    }

    if ( BARCODE_UPCE.equals( type ) ) {
      return "0425261";
    }
    if ( BARCODE_UPCA.equals( type ) ) {
      return "42526112345";
    }

    if ( BARCODE_ROYALMAIL.equals( type ) ) {
      return "B31HQ1A";
    }

    if ( BARCODE_USPSINTELLIGENTMAIL.equals( type ) ) {
      return "0123456709498765432101234567891";
    }

    return "12345678";
  }

}
