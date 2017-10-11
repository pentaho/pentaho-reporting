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

import javax.swing.table.TableModel;
import java.math.BigDecimal;

public class NumberSequence extends AbstractSequence {
  public NumberSequence() {
  }

  public SequenceDescription getSequenceDescription() {
    return new NumberSequenceDescription();
  }

  public TableModel produce( final DataRow parameters, final DataFactoryContext dataFactoryContext )
    throws ReportDataFactoryException {
    final Integer limit = getTypedParameter( "limit", Integer.class, 100 );
    final BigDecimal stepSize = getTypedParameter( "step", BigDecimal.class, new BigDecimal( 1 ) );
    final BigDecimal start = getTypedParameter( "start", BigDecimal.class, new BigDecimal( 1 ) );
    final Boolean ascending = getTypedParameter( "ascending", Boolean.class, true );

    final TypedTableModel tableModel = new TypedTableModel();
    tableModel.addColumn( "number", BigDecimal.class );
    BigDecimal value;
    if ( Boolean.TRUE.equals( ascending ) ) {
      value = start;
    } else {
      value = start.add( stepSize.multiply( new BigDecimal( limit ) ) );
    }

    for ( int i = 0; i < limit; i++ ) {
      tableModel.setValueAt( value, i, 0 );
      value = value.add( stepSize );
    }
    return tableModel;
  }
}
