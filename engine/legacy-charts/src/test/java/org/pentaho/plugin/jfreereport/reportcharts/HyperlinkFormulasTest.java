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
