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


package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.DataRow;

public class SimpleBarcodesExpressionTest {

  private static final String RAW_PARAM = "rawDataField";

  @Test
  public void testGetValue() {
    SimpleBarcodesExpression expression = spy( new SimpleBarcodesExpression() );
    Object obj = expression.getValue();
    assertThat( obj, is( nullValue() ) );

    DataRow dataRow = mock( DataRow.class );
    doReturn( dataRow ).when( expression ).getDataRow();
    doReturn( null ).when( dataRow ).get( RAW_PARAM );

    expression.setRawDataField( RAW_PARAM );
    obj = expression.getValue();
    assertThat( obj, is( nullValue() ) );

    doReturn( "test" ).when( dataRow ).get( RAW_PARAM );
    obj = expression.getValue();
    assertThat( obj, is( nullValue() ) );

    expression.setType( SimpleBarcodesUtility.BARCODE_DATAMATRIX );
    obj = expression.getValue();
    assertThat( obj, is( instanceOf( BarcodeDrawable.class ) ) );

    expression.setType( "incorrect_type" );
    obj = expression.getValue();
    assertThat( obj, is( nullValue() ) );

    expression.setType( SimpleBarcodesUtility.BARCODE_PDF417 );
    expression.setShowText( true );
    expression.setBarWidth( 10 );
    expression.setBarHeight( 20 );
    obj = expression.getValue();
    assertThat( obj, is( instanceOf( BarcodeWrapper.class ) ) );
    BarcodeWrapper bar = (BarcodeWrapper) obj;
    assertThat( bar.getBarcode(), is( notNullValue() ) );
  }
}
