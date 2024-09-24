/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
