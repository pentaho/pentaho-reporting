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

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

public class Prd3174IT extends TestCase {
  public Prd3174IT() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParameterValidation() throws ReportProcessingException {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "key", String.class );
    model.addColumn( "value", String.class );
    model.addRow( "K1", "V1" );
    model.addRow( "K2", "V2" );
    model.addRow( "K3", "V3" );

    final TableDataFactory dataFactory = new TableDataFactory();
    dataFactory.addTable( "query", model );

    final DefaultListParameter listParameter =
      new DefaultListParameter( "query", "key", "value", "parameter", true, false, String.class );

    final DefaultParameterDefinition parameterDefinition = new DefaultParameterDefinition();
    parameterDefinition.addParameterDefinition( listParameter );

    final MasterReport report = new MasterReport();
    final DefaultReportParameterValidator validator = new DefaultReportParameterValidator();
    report.setDataFactory( dataFactory );
    report.setParameterDefinition( parameterDefinition );
    report.getParameterValues().put( "parameter", new Object[] { "K1", new Integer( 1 ) } );
    ValidationResult validate = validator.validate( null, parameterDefinition, new DefaultParameterContext( report ) );
    assertFalse( validate.isEmpty() );

    report.getParameterValues().put( "parameter", new Object[] { "K1", "K2" } );
    validate = validator.validate( null, parameterDefinition, new DefaultParameterContext( report ) );
    assertTrue( validate.isEmpty() );

    report.getParameterValues().put( "parameter", new Object[] { "K1", "K2", "K5" } );
    validate = validator.validate( null, parameterDefinition, new DefaultParameterContext( report ) );
    assertTrue( validate.isEmpty() );
  }

  public void testStrictParameterValidation() throws ReportProcessingException {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( "key", String.class );
    model.addColumn( "value", String.class );
    model.addRow( "K1", "V1" );
    model.addRow( "K2", "V2" );
    model.addRow( "K3", "V3" );

    final TableDataFactory dataFactory = new TableDataFactory();
    dataFactory.addTable( "query", model );

    final DefaultListParameter listParameter =
      new DefaultListParameter( "query", "key", "value", "parameter", true, true, String.class );

    final DefaultParameterDefinition parameterDefinition = new DefaultParameterDefinition();
    parameterDefinition.addParameterDefinition( listParameter );

    final MasterReport report = new MasterReport();
    final DefaultReportParameterValidator validator = new DefaultReportParameterValidator();
    report.setDataFactory( dataFactory );
    report.setParameterDefinition( parameterDefinition );
    report.getParameterValues().put( "parameter", new Object[] { "K1", new Integer( 1 ) } );
    ValidationResult validate = validator.validate( null, parameterDefinition, new DefaultParameterContext( report ) );
    assertFalse( validate.isEmpty() );

    report.getParameterValues().put( "parameter", new Object[] { "K1", "K2" } );
    validate = validator.validate( null, parameterDefinition, new DefaultParameterContext( report ) );
    assertTrue( validate.isEmpty() );

    report.getParameterValues().put( "parameter", new Object[] { "K1", "K2", "K5" } );
    validate = validator.validate( null, parameterDefinition, new DefaultParameterContext( report ) );
    assertFalse( validate.isEmpty() );
  }
}
