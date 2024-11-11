/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
