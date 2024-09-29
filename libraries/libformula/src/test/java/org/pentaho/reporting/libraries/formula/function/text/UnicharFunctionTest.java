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
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

/**
 * @author Pawan Ponugupati
 */
public class UnicharFunctionTest extends FormulaTestBase {
    public void testDefault() throws Exception {
        runDefaultTest();
    }

    public Object[][] createDataTest() {
        return new Object[][]
                {
                        { "UNICHAR(134071)", "\uD842\uDFB7" },
                        { "UNICHAR(3114)", "ప" },
                        { "UNICHAR(2346)", "प" },
                        { "UNICHAR(2999)", "ஷ" },
                        { "UNICHAR(65)", "A" },
                        { "UNICHAR(97)", "a" },
                        { "UNICHAR(198)", "Æ" },
                        { "UNICHAR(1)", "\u0001" },

                        // Edge Cases
                        // Max Value of Unicode is 1114111 and Min Value is 1.
                        { "UNICHAR(1114111)", "\uDBFF\uDFFF" },
                        { "UNICHAR(-1)", new LibFormulaErrorValue( 502 ) },
                        { "UNICHAR(987654321)", new LibFormulaErrorValue( 502 ) },
                        { "UNICHAR(0)", new LibFormulaErrorValue( 502 ) },


                };
    }
}
