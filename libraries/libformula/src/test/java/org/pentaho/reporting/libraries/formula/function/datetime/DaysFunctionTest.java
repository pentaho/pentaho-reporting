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
 * Copyright (c) 2006 - 2023 Hitachi Vantara and Contributors.  All rights reserved.
 */
package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import java.math.BigDecimal;

public class DaysFunctionTest extends FormulaTestBase {
    public Object[][] createDataTest() {
        return new Object[][] {
                { "DAYS(DATEVALUE(\"12/30/2021\");DATEVALUE(\"1/4/2022\"))", new BigDecimal( 5 ) },
                { "DAYS(DATEVALUE(\"1/4/2022\");DATEVALUE(\"12/30/2021\"))", new BigDecimal( -5 ) },
                { "DAYS(DATEVALUE(\"1/4/2022\");DATEVALUE(\"12/30/2022\"))", new BigDecimal( 360 ) },
                { "DAYS(DATEVALUE(\"1/4/2022\");DATEVALUE(\"12/30/2020\"))", new BigDecimal( -370 ) },
                { "DAYS(DATEVALUE(\"12/30/2020\");DATEVALUE(\"1/4/2022\"))", new BigDecimal( 370 ) },
        };
    }

    public void testDefault() throws Exception{
        runDefaultTest();
    }
}