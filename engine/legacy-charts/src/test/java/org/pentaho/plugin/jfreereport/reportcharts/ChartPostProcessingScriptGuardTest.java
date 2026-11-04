/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ChartPostProcessingScriptGuardTest {
  private static final String ALLOW_SCRIPT_EVALUATION =
      "org.pentaho.reporting.engine.classic.core.allowScriptEvaluation";

  private JFreeChart chart;
  private AbstractChartExpression expression;

  @BeforeClass
  public static void ensureBootIsDone() {
    ClassicEngineBoot.getInstance().start();
  }

  @Before
  public void setUp() {
    chart = new JFreeChart( new CategoryPlot() );
    expression = new DummyCategoricalChartExpression();
    expression.setRuntime( new DebugExpressionRuntime() );
    expression.setPostProcessingLanguage( "beanshell" );
    expression.setPostProcessingScript( "chartExpression.setName( \"script-was-evaluated\" );" );
  }

  @After
  public void tearDown() {
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty( ALLOW_SCRIPT_EVALUATION, null );
  }

  @Test
  public void testScriptIsNotEvaluatedWhenDisabled() {
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty( ALLOW_SCRIPT_EVALUATION, "false" );

    assertSame( chart, expression.postProcessChart( chart ) );
    assertNull( expression.getName() );
  }

  @Test
  public void testScriptIsEvaluatedWhenEnabled() {
    ClassicEngineBoot.getInstance().getEditableConfig().setConfigProperty( ALLOW_SCRIPT_EVALUATION, "true" );

    assertSame( chart, expression.postProcessChart( chart ) );
    assertEquals( "script-was-evaluated", expression.getName() );
  }
}
