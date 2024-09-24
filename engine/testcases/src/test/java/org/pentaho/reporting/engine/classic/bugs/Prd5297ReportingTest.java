/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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

