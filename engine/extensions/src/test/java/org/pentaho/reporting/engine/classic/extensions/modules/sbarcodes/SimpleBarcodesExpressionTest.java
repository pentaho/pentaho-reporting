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
