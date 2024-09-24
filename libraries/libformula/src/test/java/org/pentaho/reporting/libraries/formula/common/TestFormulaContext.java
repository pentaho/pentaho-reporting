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

package org.pentaho.reporting.libraries.formula.common;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;
import org.pentaho.reporting.libraries.formula.lvalues.ContextLookup;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.operators.OperatorFactory;
import org.pentaho.reporting.libraries.formula.typing.ArrayCallback;
import org.pentaho.reporting.libraries.formula.typing.DefaultTypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Cedric Pronzato
 */
public class TestFormulaContext implements FormulaContext {
  private class InlineArrayCallback implements ArrayCallback {
    private final String firstColumnName;
    private final int firstRow;
    private final int firstCol;
    private final int count;

    protected InlineArrayCallback( final String firstColumnName,
                                   final int firstRow,
                                   final int firstCol,
                                   final int count ) {
      this.firstColumnName = firstColumnName;
      this.firstRow = firstRow;
      this.firstCol = firstCol;
      this.count = count;
    }

    public LValue getRaw( final int row, final int column ) throws EvaluationException {
      if ( column == 0 ) {
        final ContextLookup lookup = new ContextLookup( "." + firstColumnName + ( firstRow + row ) );
        lookup.initialize( TestFormulaContext.this );
        return lookup;
      }
      return null;
    }

    public Object getValue( final int row, final int column ) throws EvaluationException {
      // System.out.println((firstRow+row) + " col " + column + " first col " + firstCol);
      if ( column == 0 ) {
        return model.getValueAt( firstRow + row, firstCol );
      }
      throw new RuntimeException( "cannot find symbol" );
    }

    public Type getType( final int row, final int column ) throws EvaluationException {
      if ( column == 0 ) {
        return resolveReferenceType( '.' + firstColumnName + ( firstRow + row ) );
      }
      return AnyType.TYPE;
    }

    public int getColumnCount() {
      return 1;
    }

    public int getRowCount() {
      return count;
    }
  }


  public static Date createDate1( final int year, final int month, final int day, final int hour,
                                  final int minute, final int sec, final int millisec ) {
    final Calendar cal = GregorianCalendar.getInstance();
    cal.set( GregorianCalendar.YEAR, year );
    cal.set( GregorianCalendar.MONTH, month );
    cal.set( GregorianCalendar.DAY_OF_MONTH, day );
    cal.set( GregorianCalendar.HOUR_OF_DAY, hour );
    cal.set( GregorianCalendar.MINUTE, minute );
    cal.set( GregorianCalendar.SECOND, sec );
    cal.set( GregorianCalendar.MILLISECOND, millisec );
    return cal.getTime();
  }

  /*
   * id B C 3 ="7" 4 =2 4 5 =3 5 6 =1=1 7 7 ="Hello" 2005-01-31 8 2006-01-31 9
   * =1/0 02:00:00 10 =0 23:00:00 11 3 5 12 4 6 13 2005-01-31T01:00:00 8 14 1
   * 4 15 2 3 16 3 2 17 4 1
   */
  public static Date createDate1() {
    final Calendar cal = GregorianCalendar.getInstance();
    cal.set( GregorianCalendar.YEAR, 2005 );
    cal.set( GregorianCalendar.MONTH, GregorianCalendar.JANUARY );
    cal.set( GregorianCalendar.DAY_OF_MONTH, 31 );
    cal.set( GregorianCalendar.MILLISECOND, 0 );
    cal.set( GregorianCalendar.HOUR_OF_DAY, 0 );
    cal.set( GregorianCalendar.MINUTE, 0 );
    cal.set( GregorianCalendar.SECOND, 0 );
    return cal.getTime();
  }

  public static java.sql.Date createDate( final int year, final int month, final int day ) {
    final Calendar cal = GregorianCalendar.getInstance();
    cal.set( GregorianCalendar.YEAR, year );
    cal.set( GregorianCalendar.MONTH, month );
    cal.set( GregorianCalendar.DAY_OF_MONTH, day );
    cal.set( GregorianCalendar.MILLISECOND, 0 );
    cal.set( GregorianCalendar.HOUR_OF_DAY, 0 );
    cal.set( GregorianCalendar.MINUTE, 0 );
    cal.set( GregorianCalendar.SECOND, 0 );
    return new java.sql.Date( cal.getTime().getTime() );
  }

  private static class TestCaseTableModel extends AbstractTableModel {

    private Object[][] data = new Object[][]
      {
        // B , C
        { null, null }, // 0
        { null, null }, // 1
        { null, null }, // 2
        { "7", null },  // 3
        { new BigDecimal( 2 ), new BigDecimal( 4 ) }, // 4
        { new BigDecimal( 3 ), new BigDecimal( 5 ) }, // 5
        { Boolean.TRUE, new BigDecimal( 7 ) },  // 6
        { "Hello", createDate( 2005, Calendar.JANUARY, 31 ) },  // 7
        { null, createDate1( 2006, Calendar.JANUARY, 31, 0, 0, 0, 0 ) },  // 8
        { LibFormulaErrorValue.ERROR_ARITHMETIC_VALUE,
          createDate1( 0, 0, 0, 2, 0, 0, 0 ) }, // 9
        { new BigDecimal( 0 ), createDate1( 0, 0, 0, 23, 0, 0, 0 ) }, // 10
        { new BigDecimal( 3 ), new BigDecimal( 5 ) }, // 11
        { new BigDecimal( 4 ), new BigDecimal( 6 ) }, // 12
        { null, null }, // 13
        { new BigDecimal( 1 ), new BigDecimal( 4 ) }, // 14
        { new BigDecimal( 2 ), new BigDecimal( 3 ) }, // 15
        { new BigDecimal( 3 ), new BigDecimal( 2 ) }, // 16
        { new BigDecimal( 4 ), new BigDecimal( 1 ) }, // 17
        { new Object[] { new BigDecimal( 1 ), new BigDecimal( 2 ), new BigDecimal( 3 ) }, // B18
          Arrays.asList( new Object[] { new BigDecimal( 1 ), new BigDecimal( 2 ), new BigDecimal( 3 ) } ) }, // C18
        { new Object[] { new Object[ 0 ] }, // B19
          Arrays.asList( new Object[] { new ArrayList(), new BigDecimal( 42 ), new BigDecimal( 43 ) } ) }, // C19
      };

    public int getColumnCount() {
      return 2;
    }

    public String getColumnName( final int column ) {
      if ( column == 0 ) {
        return "B";
      } else if ( column == 1 ) {
        return "C";
      }
      return null;
    }

    public int getRowCount() {
      return 18;
    }

    public Object getValueAt( final int rowIndex, final int columnIndex ) {
      return data[ rowIndex ][ columnIndex ];
    }

  }

  private FormulaContext formulaContext;
  private TableModel model;
  private boolean useGuessType;
  private DefaultTypeRegistry typeRegistry;

  public static final TableModel testCaseDataset = new TestCaseTableModel();

  /**
   * Creates an empty formula context. It means that no references will be available.
   */
  public TestFormulaContext() {
    this( new DefaultTableModel(), true );
  }

  /**
   * Creates a formula context using the given model for references. The references type will always be of type
   * <code>Any</code>.
   *
   * @param model The model.
   */
  public TestFormulaContext( final TableModel model ) {
    this( model, true );
  }

  /**
   * Creates a formula context using the given model for references.
   *
   * @param model     The table model to use
   * @param guessType if <code>resolveReferenceType</code> should guess the type of the reference or return a type
   *                  <code>Any</code>.
   */
  public TestFormulaContext( final TableModel model, final boolean guessType ) {
    formulaContext = new DefaultFormulaContext
      ( LibFormulaBoot.getInstance().getGlobalConfig(), Locale.US, TimeZone.getDefault() );
    this.model = model;
    useGuessType = guessType;
    this.typeRegistry = new DefaultTypeRegistry();
    this.typeRegistry.initialize( this );
  }

  public Configuration getConfiguration() {
    return formulaContext.getConfiguration();
  }

  public FunctionRegistry getFunctionRegistry() {
    return formulaContext.getFunctionRegistry();
  }

  public LocalizationContext getLocalizationContext() {
    return formulaContext.getLocalizationContext();
  }

  public OperatorFactory getOperatorFactory() {
    return formulaContext.getOperatorFactory();
  }

  public TypeRegistry getTypeRegistry() {
    return typeRegistry;
  }

  public boolean isReferenceDirty( final Object name )
    throws EvaluationException {
    return formulaContext.isReferenceDirty( name );
  }

  public Object resolveReference( final Object name ) throws EvaluationException {
    if ( name instanceof String ) {
      final String ref = (String) name;
      final String[] split = ref.split( ":" );
      if ( split.length == 0 ) {
        return null;
      }
      // assuming references with the following format:
      // - starting with a .
      // - followed by the column name identified by one letter
      // - followed by digits representing the row number

      final String firstColumnName = split[ 0 ].substring( 1, 2 );
      int col = -1;
      for ( int i = 0; i < model.getColumnCount(); i++ ) {
        if ( firstColumnName.equalsIgnoreCase( model.getColumnName( i ) ) ) {
          col = i;
          break;
        }
      }
      final int firstCol = col;
      final int firstRow = Integer.parseInt( split[ 0 ].substring( 2 ) );

      if ( split.length == 2 ) {
        // array of reference assuming same column name
        final int secondRow = Integer.parseInt( split[ 1 ].substring( 2 ) );

        final int count = secondRow - firstRow;
        if ( count >= 0 ) {
          return new InlineArrayCallback( firstColumnName, firstRow, firstCol, count + 1 );
        } // else error
      } else {
        // one reference
        return model.getValueAt( firstRow, firstCol );
      }
    }

    return null;
  }

  public Type resolveReferenceType( final Object name )
    throws EvaluationException {
    if ( name instanceof String ) {
      final String ref = (String) name;
      final String[] split = ref.split( ":" );
      if ( split.length == 2 ) {
        return AnyType.ANY_ARRAY;
      }
    }

    if ( useGuessType ) {
      final Object value = resolveReference( name );
      return getTypeRegistry().guessTypeOfObject( value );
    } else {
      return AnyType.TYPE;
    }
  }

  public Date getCurrentDate() {
    final GregorianCalendar gcal = new GregorianCalendar( 2011, Calendar.APRIL, 7, 15, 0, 0 );
    gcal.setTimeZone( getLocalizationContext().getTimeZone() );
    return gcal.getTime();
  }
}
