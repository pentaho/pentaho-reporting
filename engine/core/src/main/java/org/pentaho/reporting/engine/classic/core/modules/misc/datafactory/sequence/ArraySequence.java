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
import java.lang.reflect.Array;

public class ArraySequence extends AbstractSequence {
  public ArraySequence() {
  }

  public SequenceDescription getSequenceDescription() {
    return new ArraySequenceDescription();
  }

  public TableModel produce( final DataRow parameters, final DataFactoryContext dataFactoryContext )
    throws ReportDataFactoryException {
    final String col = getTypedParameter( "column", String.class );
    if ( col == null ) {
      throw new ReportDataFactoryException( "Column parameter is not defined." );
    }

    final String displayName = getTypedParameter( "display-name", String.class, col );
    final Object o = parameters.get( col );
    if ( o == null || o.getClass().isArray() == false ) {
      return new TypedTableModel( new String[] { displayName } );
    }

    final TypedTableModel model =
        new TypedTableModel( new String[] { displayName }, new Class[] { o.getClass().getComponentType() } );
    final int length = Array.getLength( o );
    for ( int i = 0; i < length; i += 1 ) {
      model.addRow( Array.get( o, i ) );
    }

    return model;
  }
}
