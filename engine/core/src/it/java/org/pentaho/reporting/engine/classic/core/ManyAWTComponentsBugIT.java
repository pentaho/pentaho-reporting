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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
