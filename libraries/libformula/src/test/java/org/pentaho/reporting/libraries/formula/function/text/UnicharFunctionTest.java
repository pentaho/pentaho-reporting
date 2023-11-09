/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2023 Hitachi Vantara and Contributors.  All rights reserved.
 */

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
