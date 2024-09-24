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

package org.pentaho.plugin.jfreereport.reportcharts;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HyperlinkFormulasTest {

  @Test
  public void getFormulas() {
    final AbstractChartExpression expression = new AbstractChartExpression() {
    };
    final String[] hyperlinkFormulas = expression.getHyperlinkFormulas();
    assertNotNull( hyperlinkFormulas );
    assertEquals( 0, hyperlinkFormulas.length );
    final String urlFormula = UUID.randomUUID().toString();
    expression.setUrlFormula( urlFormula );
    final String[] hyperlinkFormulas2 = expression.getHyperlinkFormulas();
    assertNotNull( hyperlinkFormulas2 );
    assertEquals( 1, hyperlinkFormulas2.length );
    assertEquals( urlFormula, hyperlinkFormulas2[ 0 ] );
  }

}
