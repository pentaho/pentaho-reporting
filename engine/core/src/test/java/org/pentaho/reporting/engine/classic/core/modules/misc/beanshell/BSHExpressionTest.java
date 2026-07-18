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



package org.pentaho.reporting.engine.classic.core.modules.misc.beanshell;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.states.DataRowConnector;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;

import javax.swing.table.DefaultTableModel;

public class BSHExpressionTest extends TestCase {
  public BSHExpressionTest( final String s ) {
    super( s );
  }

  public void testCreate() throws Exception {
    assertTrue( DataRow.class.isAssignableFrom( DataRowConnector.class ) );
    final BSHExpression ex = new BSHExpression();
    ex.setExpression( "" );

    final DefaultProcessingContext processingContext = new DefaultProcessingContext();

    ex.setRuntime( new DebugExpressionRuntime( new DefaultTableModel(), 0, processingContext ) );
    assertNull( ex.getValue() );
    // must not crash
  }
}
