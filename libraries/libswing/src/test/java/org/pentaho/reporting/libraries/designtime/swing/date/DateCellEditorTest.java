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

package org.pentaho.reporting.libraries.designtime.swing.date;

import junit.framework.TestCase;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class DateCellEditorTest extends TestCase {

  public void testDate() {
    testCommon( Date.class );
  }

  public void testSqlDate() {
    testCommon( java.sql.Date.class );
  }

  public void testTimestamp() {
    testCommon( Timestamp.class );
  }

  public void testTime() {
    testCommon( Time.class );
  }

  private void testCommon( Class clazz ) {
    DateCellEditor editor = new DateCellEditor( clazz );
    editor.getTableCellEditorComponent( null, new Date( System.currentTimeMillis() ), true, 0, 0 );
    Object cellEditorValue = editor.getCellEditorValue();
    assertEquals( clazz, cellEditorValue.getClass() );
  }

}
