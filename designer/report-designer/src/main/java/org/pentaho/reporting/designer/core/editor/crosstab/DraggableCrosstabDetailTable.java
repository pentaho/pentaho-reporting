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

package org.pentaho.reporting.designer.core.editor.crosstab;

import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabDetail;
import org.pentaho.reporting.engine.classic.core.function.ItemCountFunction;
import org.pentaho.reporting.libraries.designtime.swing.table.PropertyTable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class DraggableCrosstabDetailTable extends PropertyTable implements FieldDragSupport {
  private UUID dragId;
  private CrosstabDetailTableModel dataModel;

  public DraggableCrosstabDetailTable( final CrosstabDetailTableModel dm ) {
    super( dm );
    dataModel = dm;
    dragId = UUID.randomUUID();

    setTransferHandler( new CrosstabDialogTransferHandler( this ) );
    setDragEnabled( true );
    setFillsViewportHeight( true );
    setDropMode( DropMode.INSERT_ROWS );
  }

  public UUID getDragId() {
    return dragId;
  }

  public void removeValues( final List<IndexedTransferable.FieldTuple> fields ) {
    final Set<Integer> indexes = new TreeSet<Integer>();
    for ( final IndexedTransferable.FieldTuple field : fields ) {
      indexes.add( field.getIndex() );
    }
    ArrayList<Integer> l = new ArrayList<Integer>( indexes );
    Collections.reverse( l );
    for ( final Integer index : indexes ) {
      dataModel.remove( index );
    }
  }

  public List<IndexedTransferable.FieldTuple> getSelectedFields() {
    int[] selectedValues = getSelectedRows();
    ArrayList<IndexedTransferable.FieldTuple> retval = new ArrayList<IndexedTransferable.FieldTuple>();
    for ( final int idx : selectedValues ) {
      CrosstabDetail dimension = dataModel.get( idx );
      retval.add( new IndexedTransferable.FieldTuple( idx, dimension.getField(), dimension.getTitle(), dimension ) );
    }
    return retval;
  }

  private boolean containsField( final String field ) {
    for ( int i = 0; i < getRowCount(); i += 1 ) {
      if ( field.equals( dataModel.get( i ).getField() ) ) {
        return true;
      }
    }
    return false;
  }

  private CrosstabDetail toDimension( final IndexedTransferable.FieldTuple tuple ) {
    final Object raw = tuple.getRaw();
    if ( raw instanceof CrosstabDetail ) {
      CrosstabDetail rawDimension = (CrosstabDetail) raw;
      return rawDimension.clone();
    } else {
      return new CrosstabDetail( tuple.getValue(), tuple.getTitle(), ItemCountFunction.class );
    }

  }

  public void insert( final TransferHandler.DropLocation dropLocation,
                      final List<IndexedTransferable.FieldTuple> items,
                      final boolean preventDuplicates ) {
    final int idx = findInsertIndex( dropLocation );
    if ( idx == -1 ) {
      for ( int i = 0; i < items.size(); i++ ) {
        final IndexedTransferable.FieldTuple tuple = items.get( i );
        String value = tuple.getValue();
        if ( preventDuplicates ) {
          if ( containsField( value ) ) {
            continue;
          }
        }
        dataModel.add( toDimension( tuple ) );
      }
    } else {
      for ( int i = items.size() - 1; i >= 0; i -= 1 ) {
        final IndexedTransferable.FieldTuple tuple = items.get( i );
        String value = tuple.getValue();
        if ( preventDuplicates ) {
          if ( containsField( value ) ) {
            continue;
          }
        }
        dataModel.add( idx, toDimension( tuple ) );
      }
    }
  }

  private int findInsertIndex( final TransferHandler.DropLocation dropLocation ) {
    if ( dropLocation instanceof DropLocation ) {
      DropLocation dl = (DropLocation) dropLocation;
      return dl.getRow();
    }
    return rowAtPoint( dropLocation.getDropPoint() );
  }
}
