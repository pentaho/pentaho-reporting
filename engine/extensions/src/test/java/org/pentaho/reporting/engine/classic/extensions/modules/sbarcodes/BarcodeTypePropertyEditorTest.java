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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BarcodeTypePropertyEditorTest {

  private static final String[] BARCODE_TYPES = new String[] { SimpleBarcodesUtility.BARCODE_2OF5,
    SimpleBarcodesUtility.BARCODE_2OF5INT, SimpleBarcodesUtility.BARCODE_CODABAR,
    SimpleBarcodesUtility.BARCODE_CODE128, SimpleBarcodesUtility.BARCODE_CODE128A,
    SimpleBarcodesUtility.BARCODE_CODE128B, SimpleBarcodesUtility.BARCODE_CODE128C,
    SimpleBarcodesUtility.BARCODE_CODE39, SimpleBarcodesUtility.BARCODE_CODE39EXT, SimpleBarcodesUtility.BARCODE_EAN13,
    SimpleBarcodesUtility.BARCODE_ISBN, SimpleBarcodesUtility.BARCODE_PDF417, SimpleBarcodesUtility.BARCODE_POSTNET,
    SimpleBarcodesUtility.BARCODE_UCCEAN128, SimpleBarcodesUtility.BARCODE_UPCA, SimpleBarcodesUtility.BARCODE_EAN8,
    SimpleBarcodesUtility.BARCODE_UPCE, SimpleBarcodesUtility.BARCODE_EAN128, SimpleBarcodesUtility.BARCODE_DATAMATRIX,
    SimpleBarcodesUtility.BARCODE_ROYALMAIL, SimpleBarcodesUtility.BARCODE_USPSINTELLIGENTMAIL };

  private BarcodeTypePropertyEditor editor = new BarcodeTypePropertyEditor();

  @Test
  public void testSetValue() {
    editor.setValue( "incorrect_value" );
    assertThat( editor.getValue(), is( nullValue() ) );

    for ( String type : BARCODE_TYPES ) {
      editor.setValue( type );
      assertThat( (String) editor.getValue(), is( equalTo( type ) ) );
      editor.setAsText( type );
      assertThat( editor.getAsText(), is( equalTo( type ) ) );
    }
  }

  @Test
  public void testIsPaintable() {
    assertThat( editor.isPaintable(), is( equalTo( false ) ) );
  }

  @Test
  public void testGetJavaInitializationString() {
    assertThat( editor.getJavaInitializationString(), is( nullValue() ) );
  }

  @Test
  public void testGetCustomEditor() {
    assertThat( editor.getCustomEditor(), is( nullValue() ) );
  }

  @Test
  public void testSupportsCustomEditor() {
    assertThat( editor.supportsCustomEditor(), is( equalTo( false ) ) );
  }

  @Test
  public void testGetTags() {
    String[] tags = editor.getTags();
    assertThat( tags, is( arrayContainingInAnyOrder( BARCODE_TYPES ) ) );
  }
}
