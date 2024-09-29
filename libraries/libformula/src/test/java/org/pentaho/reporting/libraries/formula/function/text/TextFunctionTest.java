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


package org.pentaho.reporting.libraries.formula.function.text;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author Cedric Pronzato
 */
public class TextFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    final Locale locale = getContext().getLocalizationContext().getLocale();
    final DecimalFormat format = new DecimalFormat( "#0.#######", new DecimalFormatSymbols( locale ) );
    return new Object[][]
      {
        { "TEXT(\"HI\")", "HI" },
        { "TEXT(5)", "5" },
        { "TEXT(100.01)", format.format( 100.01 ) }
      };
  }

}
