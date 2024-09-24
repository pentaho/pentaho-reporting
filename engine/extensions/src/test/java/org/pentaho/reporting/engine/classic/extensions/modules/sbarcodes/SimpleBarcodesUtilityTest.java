/*!
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
 * Copyright (c) 2005-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.barbecue.Barcode;
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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.impl.code128.EAN128;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrix;
import org.krysalis.barcode4j.impl.fourstate.RoyalMailCBC;
import org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMail;
import org.krysalis.barcode4j.impl.upcean.EAN8;
import org.krysalis.barcode4j.impl.upcean.UPCE;
import org.krysalis.barcode4j.tools.UnitConv;

public class SimpleBarcodesUtilityTest {

  private static final double BAR_HEIGHT = 500.0;
  private static final String INCORRECT_DATA = "123";
  private static final String INCORRECT_TYPE = "incorrect_type";

  private final List<BarcodeTypeInfo> barcodeTypes = new ArrayList<SimpleBarcodesUtilityTest.BarcodeTypeInfo>() {
    private static final long serialVersionUID = 2821772403284540156L;

    {
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_CODE39, Code39Barcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_CODE39EXT, Code39Barcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_CODABAR, CodabarBarcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_EAN13, EAN13Barcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_UPCA, UPCABarcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_ISBN, BooklandBarcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_CODE128, Code128Barcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_CODE128A, Code128Barcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_CODE128B, Code128Barcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_CODE128C, Code128Barcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_UCCEAN128, UCCEAN128Barcode.class, false ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_2OF5, Std2of5Barcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_2OF5INT, Int2of5Barcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_POSTNET, PostNetBarcode.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_PDF417, PDF417Barcode.class ) );
    }
  };

  private final List<BarcodeTypeInfo> barcode4JTypes = new ArrayList<SimpleBarcodesUtilityTest.BarcodeTypeInfo>() {
    private static final long serialVersionUID = -2161436323390551096L;

    {
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_DATAMATRIX, DataMatrix.class, false ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_EAN8, EAN8.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_EAN128, EAN128.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_UPCE, UPCE.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_ROYALMAIL, RoyalMailCBC.class ) );
      add( new BarcodeTypeInfo( SimpleBarcodesUtility.BARCODE_USPSINTELLIGENTMAIL, USPSIntelligentMail.class ) );
    }
  };

  @Test( expected = IllegalArgumentException.class )
  public void testCreateBarcodeWithoutData() {
    SimpleBarcodesUtility.createBarcode( null, null, false );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateBarcodeWithoutType() {
    SimpleBarcodesUtility.createBarcode( INCORRECT_DATA, null, false );
  }

  @Test
  public void testCreateBarcode() {
    Barcode barcode = SimpleBarcodesUtility.createBarcode( INCORRECT_DATA, INCORRECT_TYPE, false );
    assertThat( barcode, is( nullValue() ) );

    for ( BarcodeTypeInfo info : barcodeTypes ) {
      barcode = SimpleBarcodesUtility.createBarcode( info.getValue(), info.getType(), false );
      assertThat( barcode, is( instanceOf( info.getClazz() ) ) );
      if ( info.isCheckIncorrect() ) {
        barcode = SimpleBarcodesUtility.createBarcode( StringUtils.EMPTY, info.getType(), false );
        assertThat( barcode, is( nullValue() ) );
      }
    }
  }

  @Test
  public void testCreateBarcode4J() {
    BarcodeGenerator barcodeGen = SimpleBarcodesUtility.createBarcode4J( INCORRECT_TYPE, true, false, BAR_HEIGHT );
    assertThat( barcodeGen, is( nullValue() ) );

    for ( BarcodeTypeInfo info : barcode4JTypes ) {
      barcodeGen = SimpleBarcodesUtility.createBarcode4J( info.getType(), true, true, null );
      assertThat( barcodeGen, is( instanceOf( info.getClazz() ) ) );
      if ( info.isCheckIncorrect() ) {
        barcodeGen = SimpleBarcodesUtility.createBarcode4J( info.getType(), false, true, BAR_HEIGHT );
        assertThat( barcodeGen, is( instanceOf( info.getClazz() ) ) );
        assertThat( barcodeGen, is( instanceOf( ConfigurableBarcodeGenerator.class ) ) );
        ConfigurableBarcodeGenerator commonGen = (ConfigurableBarcodeGenerator) barcodeGen;
        assertThat( commonGen.getBean(), is( notNullValue() ) );
        assertThat( commonGen.getBean().getMsgPosition(), is( equalTo( HumanReadablePlacement.HRP_NONE ) ) );
        double expectedHeight = UnitConv.pt2mm( BAR_HEIGHT );
        assertThat( commonGen.getBean().getBarHeight(), is( equalTo( expectedHeight ) ) );
      }
    }
  }

  private class BarcodeTypeInfo {
    private String type;
    private Class<?> clazz;
    private String value;
    private boolean isCheckIncorrect = true;

    public BarcodeTypeInfo( String type, Class<?> clazz ) {
      this.type = type;
      this.clazz = clazz;
      this.value = SimpleBarcodesUtility.getBarcodeSampleData( type );
    }

    public BarcodeTypeInfo( String type, Class<?> clazz, boolean isCheckIncorrect ) {
      this( type, clazz );
      this.isCheckIncorrect = isCheckIncorrect;
    }

    public String getType() {
      return type;
    }

    public Class<?> getClazz() {
      return clazz;
    }

    public String getValue() {
      return value;
    }

    public boolean isCheckIncorrect() {
      return isCheckIncorrect;
    }

  }
}
