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
