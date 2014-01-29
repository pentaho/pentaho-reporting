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
 *  Copyright (c) 2006 - 2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.libraries.formula.util;

import org.junit.Assert;
import org.junit.Test;

public class FormulaUtilTest
{
  @Test
  public void testFormulaContextExtraction()
  {
    Object[][] test = {
        new Object[]{ "test:IF()", true, "test", "IF()"},
        new Object[]{ "=IF()", true, "report", "IF()"},
        new Object[]{ "report-IF()", false, null, null},
        new Object[]{ "=IF(:)", true, "report", "IF(:)"},
        new Object[]{ "test=:IF()", false, null, null},
        new Object[]{ "test asd:IF()", false, null, null},
    };

    for (Object[] objects : test)
    {
      String v = (String) objects[0];
      String[] strings = FormulaUtil.extractFormulaContext(v);
      if (strings[0] == null)
      {
        Assert.assertFalse("Failure in " + v, (Boolean) objects[1]);
      }
      else
      {
        Assert.assertTrue("Failure in " + v, (Boolean) objects[1]);
        Assert.assertEquals("Failure in " + v, strings[0], objects[2]);
        Assert.assertEquals("Failure in "+ v, strings[1], objects[3]);
      }
    }
  }
}
