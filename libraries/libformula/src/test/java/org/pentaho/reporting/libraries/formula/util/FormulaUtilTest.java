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

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.pentaho.reporting.libraries.formula.parser.ParseException;

public class FormulaUtilTest {
  @Test
  public void testFormulaContextExtraction() {
    Object[][] test = {
      new Object[] { "test:IF()", true, "test", "IF()" },
      new Object[] { "=IF()", true, "report", "IF()" },
      new Object[] { "report-IF()", false, null, null },
      new Object[] { "=IF(:)", true, "report", "IF(:)" },
      new Object[] { "test=:IF()", false, null, null },
      new Object[] { "test asd:IF()", false, null, null },
    };

    for ( Object[] objects : test ) {
      String v = (String) objects[ 0 ];
      String[] strings = FormulaUtil.extractFormulaContext( v );
      if ( strings[ 0 ] == null ) {
        Assert.assertFalse( "Failure in " + v, (Boolean) objects[ 1 ] );
      } else {
        assertTrue( "Failure in " + v, (Boolean) objects[ 1 ] );
        assertEquals( "Failure in " + v, strings[ 0 ], objects[ 2 ] );
        assertEquals( "Failure in " + v, strings[ 1 ], objects[ 3 ] );
      }
    }
  }

  /**
   * See PRD-5511 for details.
   * @throws ParseException
   */
  @Test
  public void getReferencesTest() throws ParseException {
    String[] references = FormulaUtil.getReferences( "=CSVTEXT([parmTerritory];FALSE();\",\";\"'\")" );
    assertTrue("References list is not empty", references.length > 0);
    assertEquals("Fromula has one reference to paramTerritory", "parmTerritory", references[0]);
  }
}
