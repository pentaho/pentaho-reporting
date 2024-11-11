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
