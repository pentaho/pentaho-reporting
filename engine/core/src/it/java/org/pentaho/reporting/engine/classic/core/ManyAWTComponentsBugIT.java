/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.elementfactory.ContentFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * This tests whether the ComponentDrawable closes the frames it creates. Running this test on Windows will yield a
 * "NoMoreHandles" error in case we messed up.
 *
 * @author Thomas Morgner
 */
public class ManyAWTComponentsBugIT extends TestCase {
  public ManyAWTComponentsBugIT() {
  }

  public ManyAWTComponentsBugIT( String string ) {
    super( string );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testAWTPrinting() throws Exception {
    if ( true || "true".equals( System.getProperty( "java.awt.headless", "false" ) ) ) {
      return;
    }

    final MasterReport report = new MasterReport();
    final ItemBand itemBand = report.getItemBand();
    final ContentFieldElementFactory cfef = new ContentFieldElementFactory();
    cfef.setFieldname( "field" );
    cfef.setMinimumWidth( new Float( 500 ) );
    cfef.setMinimumHeight( new Float( 200 ) );
    itemBand.addElement( cfef.createElement() );

    final DefaultTableModel tableModel = new DefaultTableModel( new String[] { "field" }, 2000 );
    for ( int row = 0; row < tableModel.getRowCount(); row++ ) {
      tableModel.setValueAt( new JLabel( "Value row = " + row ), row, 0 );
    }

    report.setDataFactory( new TableDataFactory( "default", tableModel ) );

    DebugReportRunner.execGraphics2D( report );
  }
}
