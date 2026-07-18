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



package org.pentaho.reporting.libraries.formula;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;

public class Prd5297Test {
  @Before
  public void setUp() throws Exception {
    LibFormulaBoot.getInstance().start();
  }

  @Test
  public void testFunctionMetaData() {
    DefaultFormulaContext ctx = new DefaultFormulaContext();
    FunctionRegistry functionRegistry = ctx.getFunctionRegistry();
    for ( final String name : functionRegistry.getFunctionNames() ) {
      FunctionDescription metaData = functionRegistry.getMetaData( name );
      Assert.assertEquals( name, metaData.getCanonicalName() );
      Assert.assertEquals( name, functionRegistry.createFunction( name ).getCanonicalName() );
    }
  }
}
