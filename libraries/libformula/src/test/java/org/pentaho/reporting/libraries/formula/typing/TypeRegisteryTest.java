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

package org.pentaho.reporting.libraries.formula.typing;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Cedric Pronzato
 */
public class TypeRegisteryTest extends TestCase {
  private FormulaContext context;

  public TypeRegisteryTest() {
  }

  public TypeRegisteryTest( final String s ) {
    super( s );
  }

  public void setUp() {
    context = new TestFormulaContext( TestFormulaContext.testCaseDataset );
    LibFormulaBoot.getInstance().start();
  }

  public void testZeroDateConvertion() throws EvaluationException {
    final Calendar cal = new GregorianCalendar
      ( context.getLocalizationContext().getTimeZone(),
        context.getLocalizationContext().getLocale() );
    cal.setTimeInMillis( 0 );

    final Date d = cal.getTime();

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Number n = typeRegistry.convertToNumber( DateTimeType.DATETIME_TYPE, d );
    assertNotNull( "The date has not been converted to a number", n );

    final Date d1 = typeRegistry.convertToDate( DateTimeType.DATETIME_TYPE, n );
    assertNotNull( "The number has not been converted to a date", d1 );
    assertEquals( "dates are different: " + d1 + " vs. " + d, d1.getTime(), d.getTime() );
  }

  public void testNowDateConvertion() throws Exception {
    final Calendar cal = new GregorianCalendar
      ( context.getLocalizationContext().getTimeZone(),
        context.getLocalizationContext().getLocale() );

    final Date d = cal.getTime();
    final Number n = context.getTypeRegistry().convertToNumber( DateTimeType.DATETIME_TYPE, d );
    assertNotNull( "The date has not been converted to a number", n );
    final Date d1 = context.getTypeRegistry().convertToDate( NumberType.GENERIC_NUMBER, n );
    assertNotNull( "The number has not been converted to a date", d1 );

    assertEquals( "dates are differents", d1.getTime(), d.getTime() );
  }

  public void testStringDateConversion() throws EvaluationException {
    final Date d = TestFormulaContext.createDate1( 2004, GregorianCalendar.JANUARY, 1, 0, 0, 0, 0 );
    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Number n = typeRegistry.convertToNumber( DateTimeType.DATE_TYPE, d );
    final Date d1 = typeRegistry.convertToDate( TextType.TYPE, "2004-01-01" );

    if ( d1.getTime() != d.getTime() ) {
      final Number n2 = typeRegistry.convertToNumber( DateTimeType.DATE_TYPE, d );
      final Date dx = typeRegistry.convertToDate( TextType.TYPE, "2004-01-01" );
    }

    assertEquals( "dates are different", d1.getTime(), d.getTime() );
  }


  public void testStringNumberConversion() throws EvaluationException {
    final Number d = new Double( 2000.5 );
    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final Number n = typeRegistry.convertToNumber( NumberType.GENERIC_NUMBER, d );
    final Number d1 = typeRegistry.convertToNumber( TextType.TYPE, "2000.5" );

    if ( d1.doubleValue() != d.doubleValue() ) {
      final Number n2 = typeRegistry.convertToNumber( DateTimeType.DATE_TYPE, d );
      final Date dx = typeRegistry.convertToDate( TextType.TYPE, "2004-01-01" );
    }

    assertEquals( "dates are different", d1.doubleValue(), d.doubleValue(), 0.0 );
  }
}
