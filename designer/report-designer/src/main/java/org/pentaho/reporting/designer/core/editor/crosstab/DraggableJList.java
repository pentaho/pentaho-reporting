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

import org.pentaho.reporting.libraries.designtime.swing.bulk.DefaultBulkListModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class DraggableJList extends JList implements FieldDragSupport {
  private DefaultBulkListModel bulkModel;
  private UUID dragId;

  public DraggableJList( final DefaultBulkListModel dataModel ) {
    super( dataModel );
    bulkModel = dataModel;
    dragId = UUID.randomUUID();

    setTransferHandler( new CrosstabDialogTransferHandler( this ) );
    setDragEnabled( true );
    setDropMode( DropMode.ON );
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
      bulkModel.remove( index );
    }
  }

  public List<IndexedTransferable.FieldTuple> getSelectedFields() {
    int[] selectedValues = getSelectedIndices();
    ArrayList<IndexedTransferable.FieldTuple> retval = new ArrayList<IndexedTransferable.FieldTuple>();
    for ( final int idx : selectedValues ) {
      retval.add( new IndexedTransferable.FieldTuple( idx, String.valueOf( bulkModel.get( idx ) ) ) );
    }
    return retval;
  }

  public void insert( final TransferHandler.DropLocation point,
                      final List<IndexedTransferable.FieldTuple> items,
                      final boolean preventDuplicates ) {
    final int idx = getDropLocation().getIndex();
    if ( idx == -1 ) {
      for ( int i = 0; i < items.size(); i++ ) {
        final IndexedTransferable.FieldTuple tuple = items.get( i );
        String value = tuple.getValue();
        if ( preventDuplicates ) {
          if ( bulkModel.contains( value ) ) {
            continue;
          }
        }
        bulkModel.addElement( value );
      }
    } else {
      for ( int i = items.size() - 1; i >= 0; i -= 1 ) {
        final IndexedTransferable.FieldTuple tuple = items.get( i );
        String value = tuple.getValue();
        if ( preventDuplicates ) {
          if ( bulkModel.contains( value ) ) {
            continue;
          }
        }
        bulkModel.add( idx, value );
      }
    }
  }
}
