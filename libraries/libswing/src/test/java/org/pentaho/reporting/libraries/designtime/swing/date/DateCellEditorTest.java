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
