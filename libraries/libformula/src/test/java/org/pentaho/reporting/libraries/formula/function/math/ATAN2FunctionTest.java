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
* Copyright (c) 2008 - 2023 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.math;

import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * @author Shiva Krishna Kurremula
 */
public class ATAN2FunctionTest extends FormulaTestBase {
  private FormulaContext context;

  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public Object[][] createDataTest() {
    return new Object[][] {
      { "ATAN2(5;10)", NumberUtil.performTuneRounding( new BigDecimal( 1.1071487177940904089723517245147377 ) ) },
      { "ATAN2(10;5)", NumberUtil.performTuneRounding( new BigDecimal( 0.4636476090008060935154787784995278 ) ) },
      { "ATAN2(10;10)", NumberUtil.performTuneRounding( new BigDecimal( 0.7853981633974482789994908671360463 ) ) },
      { "ATAN2(30;45)", NumberUtil.performTuneRounding( new BigDecimal( 0.9827937232473290540823995797836687 ) ) },
      { "ATAN2(45;30)", NumberUtil.performTuneRounding( new BigDecimal( 0.5880026035475675039165821544884238 ) ) },
      { "ATAN2(90;30)", NumberUtil.performTuneRounding( new BigDecimal( 0.3217505543966421854840120886365184 ) ) },
      { "ATAN2(30;90)", NumberUtil.performTuneRounding( new BigDecimal( 1.2490457723982544280261208768934011 ) ) },
      { "ATAN2(90;45)", NumberUtil.performTuneRounding( new BigDecimal( 0.4636476090008060935154787784995278 ) ) },
      { "ATAN2(45;90)", NumberUtil.performTuneRounding( new BigDecimal( 1.1071487177940904089723517245147377 ) ) },
      { "ATAN2(90;90)", NumberUtil.performTuneRounding( new BigDecimal( 0.7853981633974482789994908671360462 ) ) },
    };
  }
}

