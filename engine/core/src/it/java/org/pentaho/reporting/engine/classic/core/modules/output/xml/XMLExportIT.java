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

package org.pentaho.reporting.engine.classic.core.modules.output.xml;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import javax.swing.table.DefaultTableModel;

public class XMLExportIT extends TestCase {
  public XMLExportIT() {
  }

  public XMLExportIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testExport() throws Exception {
    final MasterReport report = new MasterReport();
    final ItemBand itemBand = report.getItemBand();
    final TextFieldElementFactory cfef = new TextFieldElementFactory();
    cfef.setFieldname( "field" );
    cfef.setMinimumWidth( new Float( 500 ) );
    cfef.setMinimumHeight( new Float( 200 ) );
    itemBand.addElement( cfef.createElement() );

    final DefaultTableModel tableModel = new DefaultTableModel( new String[] { "field" }, 2000 );
    for ( int row = 0; row < tableModel.getRowCount(); row++ ) {
      tableModel.setValueAt( "Value row = " + row, row, 0 );
    }

    report.setDataFactory( new TableDataFactory( "default", tableModel ) );

    DebugReportRunner.createDataXML( report );
  }
}
