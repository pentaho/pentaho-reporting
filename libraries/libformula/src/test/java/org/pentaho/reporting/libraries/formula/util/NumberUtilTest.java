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


package org.pentaho.reporting.libraries.formula.util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class NumberUtilTest {

  @Test
  public void testPerformMinuteRounding() {
    BigDecimal[][] test = {
      // =MINUTE(timevalue("00:00:59"))
      new BigDecimal[] { new BigDecimal( 0.9833333333333333333333333333333333333760 ), new BigDecimal( 0 ) },
      // =MINUTE(timevalue("00:01:00")) =MINUTE("00:01:00") =MINUTE(1/(24*60))
      new BigDecimal[] { new BigDecimal( 0.9999999999999999999999999999999999999360 ), new BigDecimal( 1 ) },
      // =MINUTE(timevalue("00:01:59"))
      new BigDecimal[] { new BigDecimal( 1.9833333333333333333333333333333333333120 ), new BigDecimal( 1 ) },
      // =MINUTE(timevalue("00:07:00"))
      new BigDecimal[] { new BigDecimal( 6.9999999999999999999999999999999999999840 ), new BigDecimal( 7 ) },
      // =MINUTE(timevalue("00:09:00"))
      new BigDecimal[] { new BigDecimal( 9.0000000000000000000000000000000000000000 ), new BigDecimal( 9 ) },
      // =MINUTE(timevalue("00:11:00"))
      new BigDecimal[] { new BigDecimal( 11.9833333333333333333333333333333333333920 ), new BigDecimal( 11 ) },
      // =MINUTE(timevalue("00:23:00"))
      new BigDecimal[] { new BigDecimal( 22.9999999999999999999999999999999999999680 ), new BigDecimal( 23 ) },
      // =MINUTE(timevalue("00:28:59"))
      new BigDecimal[] { new BigDecimal( 28.9833333333333333333333333333333333333120 ), new BigDecimal( 28 ) }
    };

    for ( BigDecimal[] bigDecimal : test ) {
      BigDecimal result = NumberUtil.performMinuteRounding( bigDecimal[ 0 ] );
      assertEquals( bigDecimal[ 1 ], result );
    }
  }
}
