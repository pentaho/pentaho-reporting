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

