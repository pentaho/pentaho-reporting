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

package org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.itext;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Row;
import com.lowagie.text.Table;
import com.lowagie.text.rtf.document.RtfDocument;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PatchRtfTableTest {

  @Test
  public void testImportTableRemovesUnusedRow() throws BadElementException {
    Table table = Mockito.mock( Table.class );
    Row row = Mockito.mock( Row.class );
    RtfDocument rtfDocument = new RtfDocument();
    List<Row> rows = new ArrayList<>( 5 );
    for ( int i = 0; i < 5; i++ ) {
      rows.add( row );
    }
    Iterator<Row> iterator = rows.iterator();

    Mockito.when( table.iterator() ).thenReturn( iterator );

    new PatchRtfTable( rtfDocument, table );
    Assert.assertFalse( iterator.hasNext() );
    Assert.assertEquals( 0, rows.size() );
  }
}
