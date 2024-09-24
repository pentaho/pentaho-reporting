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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.datetime;

import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;

import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Cedric Pronzato
 */
public class DateValueFunctionTest extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "DATEVALUE(\"2004-12-25\")=DATE(2004;12;25)", Boolean.TRUE },
        { "DATEVALUE(DATE(2004; 12; 26) - 1)=DATE(2004;12;25)", Boolean.TRUE },
        { "DATEVALUE(\"\" & (DATE(2004; 12; 26) - 1))=DATE(2004;12;25)", Boolean.TRUE },
      };
  }

  public void testFrenchDateParsing() throws Exception {
    final DefaultFormulaContext context = new DefaultFormulaContext( LibFormulaBoot.getInstance().getGlobalConfig(),
      Locale.FRENCH, TimeZone.getDefault() );

    performTest( "DATEVALUE(\"25/12/2004\")=DATE(2004;12;25)", Boolean.TRUE, context );
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }


}
