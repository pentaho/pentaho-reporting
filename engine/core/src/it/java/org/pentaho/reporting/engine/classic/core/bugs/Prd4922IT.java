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


package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class Prd4922IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private static class TestEnv extends DefaultReportEnvironment {
    private TestEnv( final Configuration configuration ) {
      super( configuration );
    }

    public Object getEnvironmentProperty( final String key ) {
      if ( "test-column".equals( key ) ) {
        return "environment";
      }
      return super.getEnvironmentProperty( key );
    }
  }

  private TableDataFactory create() {
    TypedTableModel model = new TypedTableModel();
    model.addColumn( "test-column", String.class );
    model.addRow( "table" );
    return new TableDataFactory( "query", model );
  }

  @Test
  public void testEvaluationOrderEnvBeforeParameter() {
    MasterReport report = new MasterReport();
    report.setReportEnvironment( new TestEnv( report.getConfiguration() ) );

    DefaultParameterDefinition mdef = new DefaultParameterDefinition();
    mdef.addParameterDefinition( new PlainParameter( "test-column", String.class ) );
    report.setParameterDefinition( mdef );
    report.getParameterValues().put( "test-column", "parameter" );
    report.addExpression( new ValidateValueExpression( "parameter" ) );
    DebugReportRunner.execGraphics2D( report );
  }

  @Test
  public void testEvaluationOrderParameterBeforeTable() {
    MasterReport report = new MasterReport();

    DefaultParameterDefinition mdef = new DefaultParameterDefinition();
    mdef.addParameterDefinition( new PlainParameter( "test-column", String.class ) );
    report.setParameterDefinition( mdef );
    report.getParameterValues().put( "test-column", "parameter" );

    report.setDataFactory( create() );
    report.setQuery( "query" );
    report.addExpression( new ValidateValueExpression( "table" ) );
    DebugReportRunner.execGraphics2D( report );
  }

  private static class SingleValueExpression extends AbstractExpression {
    private String value;

    private SingleValueExpression( String value ) {
      setName( "test-column" );
      this.value = value;
    }

    public Object getValue() {
      return value;
    }
  }

  @Test
  public void testEvaluationOrderTableBeforeExpression() {
    MasterReport report = new MasterReport();
    report.setDataFactory( create() );
    report.setQuery( "query" );
    report.addExpression( new SingleValueExpression( "expression" ) );
    report.addExpression( new ValidateValueExpression( "expression" ) );
    DebugReportRunner.execGraphics2D( report );
  }

  public SubReport createSubReport() {
    SubReport report = new SubReport();
    report.addExpression( new SingleValueExpression( "subreport" ) );
    report.setInputMappings( new ParameterMapping[] { new ParameterMapping( "test-column", "test-column" ) } );
    report.setExportMappings( new ParameterMapping[] { new ParameterMapping( "test-column", "test-column" ) } );
    return report;
  }

  // ignored test for now, fails to see correct value. But as we all know, export parameters are broken.
  public void testEvaluationOrderExpressionBeforeSubReportImport() {
    MasterReport report = new MasterReport();
    report.addExpression( new SingleValueExpression( "expression" ) );
    report.getReportHeader().addSubReport( createSubReport() );
    Object[] result = new Object[1];
    report.addExpression( new ValidateLazyValueExpression( "subreport", result ) );
    DebugReportRunner.execGraphics2D( report );
    Assert.assertEquals( Boolean.TRUE, result[0] );
  }

  private static class ValidateValueExpression extends AbstractExpression {
    private String expected;

    private ValidateValueExpression( String expected ) {
      this.expected = expected;
      setName( "validate" );
    }

    public Object getValue() {
      Assert.assertEquals( expected, getDataRow().get( "test-column" ) );
      return true;
    }
  }

  private static class ValidateLazyValueExpression extends AbstractExpression {
    private String expected;
    private Object[] result;

    private ValidateLazyValueExpression( String expected, Object[] result ) {
      this.expected = expected;
      this.result = result;
      setName( "validate" );
    }

    public Object getValue() {
      if ( ObjectUtilities.equal( expected, getDataRow().get( "test-column" ) ) ) {
        result[0] = true;
      }
      return true;
    }
  }
}
