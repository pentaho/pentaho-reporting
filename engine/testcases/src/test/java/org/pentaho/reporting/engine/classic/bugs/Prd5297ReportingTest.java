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


package org.pentaho.reporting.engine.classic.bugs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContextFactory;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;

import java.util.Locale;
import java.util.TimeZone;

public class Prd5297ReportingTest {

  @Before
  public void setUp() throws Exception {
    LibFormulaBoot.getInstance().start();
  }

  @Test
  public void testFunctionMetaData() {
    FormulaContext ctx = DefaultFormulaContextFactory.INSTANCE.create( Locale.US, TimeZone.getTimeZone( "UTC" ) );

    FunctionRegistry functionRegistry = ctx.getFunctionRegistry();
    for ( final String name : functionRegistry.getFunctionNames() ) {
      FunctionDescription metaData = functionRegistry.getMetaData( name );
      if ( metaData.getClass().getName().startsWith( "org.pentaho.metadata" ) ) {
        continue;
      }

      Assert.assertEquals( metaData.getClass().getName(), name, metaData.getCanonicalName() );
      Assert.assertEquals( name, functionRegistry.createFunction( name ).getCanonicalName() );
    }
  }
}

