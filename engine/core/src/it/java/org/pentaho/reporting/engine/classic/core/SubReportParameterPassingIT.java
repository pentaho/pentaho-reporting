/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.StaticDataFactory;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Creation-Date: Jan 9, 2007, 6:34:07 PM
 *
 * @author Thomas Morgner
 */
public class SubReportParameterPassingIT extends TestCase {
  public SubReportParameterPassingIT( String string ) {
    super( string );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParameterPassing() throws Exception {

    MasterReport report = new MasterReport();
    StaticDataFactory staticDataFactory = new StaticDataFactory();
    report.setDataFactory( staticDataFactory );
    report.setQuery( SubReportParameterPassingIT.class.getName() + "#createMainTableModel()" );

    Element textElement =
        TextFieldElementFactory.createStringElement( "reportField1", new Rectangle( 0, 0, 100, 20 ), Color.BLACK,
            ElementAlignment.LEFT, ElementAlignment.TOP, new FontDefinition( "Arial", 12 ), "-", "c1" );
    report.getItemBand().addElement( textElement );

    SubReport subReport = new SubReport();
    subReport.addInputParameter( "c1", "c1" );
    subReport.setQuery( SubReportParameterPassingIT.class.getName() + "#createSubReportTableModel(c1)" );
    Element subReportTextElement =
        TextFieldElementFactory.createStringElement( "subreportField1", new Rectangle( 20, 0, 100, 20 ), Color.RED,
            ElementAlignment.LEFT, ElementAlignment.TOP, new FontDefinition( "Arial", 12 ), "-", "t1" );
    subReport.getItemBand().addElement( subReportTextElement );

    report.getItemBand().addSubReport( subReport );

    DebugReportRunner.execGraphics2D( report );

  }

  public static TableModel createMainTableModel() {
    return new DefaultTableModel( new String[][] { { "1.1", "1.2" }, { "2.1", "2.2" } }, new String[] { "c1", "c2" } );
  }

  public static TableModel createSubReportTableModel( String param1 ) {
    assertNotNull( param1 );
    return new DefaultTableModel( new String[][] { { "1.1:" + param1, "1.2:" + param1 },
      { "2.1:" + param1, "2.2:" + param1 } }, new String[] { "t1", "t2" } );
  }
}
