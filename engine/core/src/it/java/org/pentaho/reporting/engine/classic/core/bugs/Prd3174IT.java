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
