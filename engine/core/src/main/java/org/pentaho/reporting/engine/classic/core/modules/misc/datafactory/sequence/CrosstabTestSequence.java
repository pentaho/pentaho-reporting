/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence;

import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.table.TableModel;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Random;

/**
 * @noinspection UnnecessaryLocalVariable
 */
public class CrosstabTestSequence extends AbstractSequence {
  public CrosstabTestSequence() {
  }

  public SequenceDescription getSequenceDescription() {
    return new CrosstabTestSequenceDescription();
  }

  public TableModel produce( final DataRow parameters, final DataFactoryContext dataFactoryContext )
    throws ReportDataFactoryException {

    final Integer colDimsRaw = getTypedParameter( "column-dimensions", Integer.class, Integer.valueOf( 3 ) );
    if ( colDimsRaw == null ) {
      throw new ReportDataFactoryException( "Parameter column-dimensions is not defined." );
    }
    if ( colDimsRaw < 1 ) {
      throw new ReportDataFactoryException( "Parameter column-dimensions has an invalid value." );
    }
    final int colDims = colDimsRaw;
    final Integer[] colCardsRaw = getTypedParameter( "column-cardinality", Integer[].class, null );
    final String[] colPatternRaw = getTypedParameter( "column-pattern", String[].class, null );

    final Integer rowDimsRaw = getTypedParameter( "row-dimensions", Integer.class, Integer.valueOf( 3 ) );
    if ( rowDimsRaw == null ) {
      throw new ReportDataFactoryException( "Parameter row-dimensions is not defined." );
    }
    if ( rowDimsRaw < 1 ) {
      throw new ReportDataFactoryException( "Parameter row-dimensions has an invalid value." );
    }
    final int rowDims = rowDimsRaw;
    final Integer[] rowCardsRaw = getTypedParameter( "row-cardinality", Integer[].class, null );
    final String[] rowPatternRaw = getTypedParameter( "row-pattern", String[].class, null );

    final int[] rowCards = populateCardinality( rowCardsRaw, rowDims );
    final int[] colCards = populateCardinality( colCardsRaw, colDims );
    final int rowCount = computeRowCount( rowCards, colCards );
    final int colCount = rowDims + colDims + 1;
    final String[] rowPattern = populatePatterns( "Row-", rowPatternRaw, rowDims );
    final String[] colPattern = populatePatterns( "Col-", colPatternRaw, colDims );

    final TypedTableModel model = new TypedTableModel( rowCount, colCount );
    for ( int r = 0; r < rowDims; r += 1 ) {
      model.addColumn( "r" + r, String.class );
    }
    for ( int c = 0; c < colDims; c += 1 ) {
      model.addColumn( "c" + c, String.class );
    }
    model.addColumn( "value", Double.class );

    final Long seed = getTypedParameter( "random-seed", Long.class, System.currentTimeMillis() );

    final Random r = new Random( seed );
    final Object[] values = new Object[rowDims + colDims + 1];
    final int[] cards = new int[rowDims + colDims];
    Arrays.fill( cards, -1 );
    int pos = 0;
    while ( pos >= 0 ) {
      if ( pos == cards.length ) {
        values[pos] = r.nextDouble();
        model.addRow( values );

        pos -= 1;
        continue;
      }

      cards[pos] += 1;
      values[pos] = MessageFormat.format( queryPattern( rowPattern, colPattern, pos ), cards[pos] );

      final int maxCard = queryCardinality( rowCards, colCards, pos );
      if ( cards[pos] >= maxCard ) {
        cards[pos] = -1;
        pos -= 1;
        continue;
      }

      pos += 1;
    }

    return model;
  }

  private int computeRowCount( final int[] rowCards, final int[] colCards ) {
    int rowCount = 1;
    for ( int i = 0; i < rowCards.length; i++ ) {
      int rowCard = rowCards[i];
      rowCount *= rowCard;
    }
    for ( int i = 0; i < colCards.length; i++ ) {
      int rowCard = colCards[i];
      rowCount *= rowCard;
    }
    return rowCount;
  }

  private int queryCardinality( final int[] rows, final int[] cols, int pos ) {
    if ( pos < rows.length ) {
      return rows[pos];
    }
    pos -= rows.length;
    if ( pos < cols.length ) {
      return cols[pos];
    }
    return -1;
  }

  private String queryPattern( final String[] rows, final String[] cols, int pos ) {
    if ( pos < rows.length ) {
      return rows[pos];
    }
    pos -= rows.length;
    if ( pos < cols.length ) {
      return cols[pos];
    }
    return null;
  }

  private int[] populateCardinality( final Integer[] array, final int size ) {
    final int[] retval = new int[size];
    Arrays.fill( retval, 1 );

    if ( array == null ) {
      return retval;
    }

    for ( int i = 0; i < Math.min( array.length, size ); i++ ) {
      final Integer integer = array[i];
      if ( integer != null ) {
        retval[i] = Math.max( integer.intValue(), 1 );
      }
    }
    return retval;
  }

  private String toSequenceChar( final int value ) {
    final int size = 'Z' - 'A';
    if ( value <= size ) {
      return String.valueOf( (char) ( 'A' + value ) );
    }
    return String.valueOf( value - size - 1 );
  }

  private String[] populatePatterns( final String prefix, final String[] array, final int size ) {
    final String[] retval = new String[size];
    for ( int i = 0; i < retval.length; i++ ) {

      retval[i] = prefix + toSequenceChar( i ) + "-{0}";
    }

    if ( array == null ) {
      return retval;
    }

    for ( int i = 0; i < Math.min( array.length, size ); i++ ) {
      final String pattern = array[i];
      if ( StringUtils.isEmpty( pattern ) == false ) {
        retval[i] = pattern;
      }
    }
    return retval;
  }
}
