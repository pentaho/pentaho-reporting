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
