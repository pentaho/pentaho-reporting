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


package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;

import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Cedric Pronzato
 */
public class DateValueFunctionTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "DATEVALUE(\"2004-12-25\")=DATE(2004;12;25)", Boolean.TRUE },
        { "DATEVALUE(DATE(2004; 12; 26) - 1)=DATE(2004;12;25)", Boolean.TRUE },
        { "DATEVALUE(\"\" & (DATE(2004; 12; 26) - 1))=DATE(2004;12;25)", Boolean.TRUE },
      };
  }

  public void testFrenchDateParsing() throws Exception {
    final DefaultFormulaContext context = new DefaultFormulaContext( LibFormulaBoot.getInstance().getGlobalConfig(),
      Locale.FRENCH, TimeZone.getDefault() );

    performTest( "DATEVALUE(\"25/12/2004\")=DATE(2004;12;25)", Boolean.TRUE, context );
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }


}
