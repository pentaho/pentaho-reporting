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
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import javax.swing.table.DefaultTableModel;

public class Prd5041IT {
  private static class DependencyFunction extends AbstractFunction {
    private ReportStateKey key;

    private DependencyFunction( final int dependencyLevel, final String name ) {
      setName( name );
      setDependencyLevel( dependencyLevel );
    }

    protected void validateHook() {
    }

    public void reportInitialized( final ReportEvent event ) {
      key = event.getState().getProcessKey();
      validateHook();
    }

    public void reportStarted( final ReportEvent event ) {
      key = event.getState().getProcessKey();
      validateHook();
    }

    public void reportFinished( final ReportEvent event ) {
      key = event.getState().getProcessKey();
      validateHook();
    }

    public void groupStarted( final ReportEvent event ) {
      key = event.getState().getProcessKey();
      validateHook();
    }

    public void groupFinished( final ReportEvent event ) {
      key = event.getState().getProcessKey();
      validateHook();
    }

    public void itemsAdvanced( final ReportEvent event ) {
      key = event.getState().getProcessKey();
      validateHook();
    }

    public void itemsStarted( final ReportEvent event ) {
      key = event.getState().getProcessKey();
      validateHook();
    }

    public void itemsFinished( final ReportEvent event ) {
      key = event.getState().getProcessKey();
      validateHook();
    }

    public void reportDone( final ReportEvent event ) {
      key = event.getState().getProcessKey();
      validateHook();
    }

    public void summaryRowSelection( final ReportEvent event ) {
      key = event.getState().getProcessKey();
      validateHook();
    }

    public Object getValue() {
      return key;
    }
  }

  private static class ForwardExpression extends AbstractExpression {
    private String field;

    private ForwardExpression( final int depLevel, final String name, final String field ) {
      this.field = field;
      setName( name );
      setDependencyLevel( depLevel );
    }

    public Object getValue() {
      return getDataRow().get( field );
    }
  }

  private static class ValidateFunction extends DependencyFunction {
    private String field;

    private ValidateFunction( final int depLevel, final String name, final String field ) {
      super( depLevel, name );
      this.field = field;
    }

    protected void validateHook() {
      final Object o = getDataRow().get( field );
      Assert.assertEquals( o, getValue() );
    }
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testDependenciesSingleLevel() throws ReportProcessingException {
    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( "query", new DefaultTableModel( 2, 2 ) ) );
    report.addExpression( new DependencyFunction( 1, "source" ) );
    report.addExpression( new ForwardExpression( 1, "forward", "source" ) );
    report.addExpression( new ValidateFunction( 1, "validate", "forward" ) );

    DebugReportRunner.createPDF( report );
  }

  @Test
  public void testDependenciesMultiLevel() throws ReportProcessingException {
    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( "query", new DefaultTableModel( 2, 2 ) ) );
    report.addExpression( new DependencyFunction( 1, "source" ) );
    report.addExpression( new ForwardExpression( 1, "forward", "source" ) );
    report.addExpression( new ValidateFunction( 0, "validate", "forward" ) );

    DebugReportRunner.createPDF( report );
  }

  @Test
  public void testDependenciesMultiLevel2() throws ReportProcessingException {
    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( "query", new DefaultTableModel( 2, 2 ) ) );
    report.addExpression( new DependencyFunction( 1, "source" ) );
    report.addExpression( new ForwardExpression( 0, "forward", "source" ) );
    report.addExpression( new ValidateFunction( 0, "validate", "forward" ) );

    DebugReportRunner.createPDF( report );
  }

  @Test
  public void testDependenciesTotalLevel() throws ReportProcessingException {
    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( "query", new DefaultTableModel( 2, 2 ) ) );
    report.addExpression( new DependencyFunction( 2, "source" ) );
    report.addExpression( new ForwardExpression( 1, "forward", "source" ) );
    report.addExpression( new ValidateFunction( 0, "validate", "forward" ) );

    DebugReportRunner.createPDF( report );
  }
}
