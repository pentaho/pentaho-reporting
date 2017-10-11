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

package org.pentaho.reporting.engine.classic.core.parameters;

import javax.swing.table.DefaultTableModel;
import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;

public class DefaultReportParameterValidatorTest extends TestCase {
  public DefaultReportParameterValidatorTest() {
  }

  public DefaultReportParameterValidatorTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSelectDefault() throws ReportProcessingException {
    final DefaultTableModel tableModel = new DefaultTableModel( new String[] { "key", "value" }, 1 );
    tableModel.setValueAt( "key-entry", 0, 0 );
    tableModel.setValueAt( "value-entry", 0, 1 );

    final DefaultListParameter listParameter =
      new DefaultListParameter( "test", "key", "value", "name", false, true, String.class );
    listParameter.setParameterAutoSelectFirstValue( true );
    listParameter.setMandatory( true );

    final DefaultParameterDefinition definition = new DefaultParameterDefinition();
    definition.addParameterDefinition( listParameter );

    final MasterReport report = new MasterReport();
    report.setParameterDefinition( definition );
    report.setDataFactory( new TableDataFactory( "test", tableModel ) );

    final DefaultParameterContext paramContext = new DefaultParameterContext( report );

    final DefaultReportParameterValidator validator = new DefaultReportParameterValidator();
    final ValidationResult result = validator.validate( new ValidationResult(), definition, paramContext );
    assertTrue( result.isEmpty() );

  }
}
