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

import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author Cedric Pronzato
 */
public class DateFunctionTest extends FormulaTestBase {

  private TimeZone defaultTz;

  @Override protected void setUp() throws Exception {
    super.setUp();
    defaultTz = TimeZone.getDefault();
    TimeZone.setDefault( TimeZone.getTimeZone( "UTC" ) );
  }

  @Override protected void tearDown() throws Exception {
    super.tearDown();
    TimeZone.setDefault( defaultTz );
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "DATE(2005;1;31)=[.C7]", Boolean.TRUE },
        { "DATE(2005;12;31)-DATE(1904;1;1)", new BigDecimal( 37255 ) },
        { "DATE(2004;2;29)=DATE(2004;2;28)+1", Boolean.TRUE },
        { "DATE(2000;2;29)=DATE(2000;2;28)+1", Boolean.TRUE },
        { "DATE(2005;3;1)=DATE(2005;2;28)+1", Boolean.TRUE },
        { "DATE(2017.5; 1;2)=DATE(2017; 1; 2)", Boolean.TRUE },
        { "DATE(2006; 2.5;3)=DATE(2006; 2; 3)", Boolean.TRUE },
        { "DATE(2006; 1;3.5)=DATE(2006; 1; 3)", Boolean.TRUE },
        { "DATE(2006; 13; 3)=DATE(2007;1; 3)", Boolean.TRUE },
        { "DATE(2006; 1; 32)=DATE(2006;2; 1)", Boolean.TRUE },
        { "DATE(2006; 25;34)=DATE(2008;2;3)", Boolean.TRUE },
        { "DATE(2006;-1;1)=DATE(2005;11;1)", Boolean.TRUE },
        { "DATE(2006;4;-1)=DATE(2006;3;30)", Boolean.TRUE },
        { "DATE(2006;-4;-1)=DATE(2005;7;30)", Boolean.TRUE },
        { "DATE(2003;2;29)=DATE(2003;3;1)", Boolean.TRUE },
      };
  }

  public void testDebugDates() throws Exception {
    Formula formula = null;
    formula = new Formula( "DATE(2005;1;31)" );
    formula.initialize( getContext() );
    Date eval = (Date) formula.evaluate();
    final DateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
    System.out.println( eval.getClass().getName() + ":" + df.format( eval ) + ";" + eval.getTime() );

    final Date date = (Date) getContext().resolveReference( ".C7" );
    System.out.println( df.format( date ) + ";" + date.getTime() );
    assertEquals( date, eval );
  }

  public void testGregorianCalendar() {
    final Date d1 = new GregorianCalendar( 2006, -1, 1 ).getTime();
    final Date d2 = new GregorianCalendar( 2005, 11, 1 ).getTime();

    assertEquals( d1, d2 );
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }


}
