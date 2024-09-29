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


package org.pentaho.reporting.designer.core.editor.crosstab;

import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CrosstabDialogTransferHandler extends TransferHandler {
  private FieldDragSupport targetList;
  private boolean fieldPool;

  public CrosstabDialogTransferHandler( final FieldDragSupport targetList ) {
    this( targetList, false );
  }

  public CrosstabDialogTransferHandler( final FieldDragSupport targetList,
                                        final boolean fieldPool ) {
    this.targetList = targetList;
    this.fieldPool = fieldPool;
  }

  public boolean importData( final TransferSupport support ) {
    if ( support.isDataFlavorSupported( IndexedTransferable.ELEMENT_FLAVOR ) == false ) {
      return false;
    }

    if ( support.isDrop() == false ) {
      return false;
    }

    try {
      final IndexedTransferable.TupleContainer items = extractFields( support.getTransferable() );
      if ( IndexedTransferable.EMPTY.equals( items ) ) {
        return false;
      }
      if ( items.getSourceId().equals( targetList.getDragId() ) ) {
        return false;
      }

      final DropLocation dropLocation = support.getDropLocation();
      targetList.insert( dropLocation, Arrays.asList( items.getTuples() ), fieldPool );
      return true;
    } catch ( final Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
      return false;
    }
  }

  private IndexedTransferable.TupleContainer extractFields( final Transferable t )
    throws IOException, UnsupportedFlavorException {
    final Object transferData = t.getTransferData( IndexedTransferable.ELEMENT_FLAVOR );
    if ( transferData instanceof IndexedTransferable.TupleContainer == false ) {
      return IndexedTransferable.EMPTY;
    }

    return (IndexedTransferable.TupleContainer) transferData;
  }

  public boolean canImport( final TransferSupport support ) {
    if ( support.isDrop() == false ) {
      return false;
    }
    if ( support.isDataFlavorSupported( IndexedTransferable.ELEMENT_FLAVOR ) ) {
      try {
        Object transferData = support.getTransferable().getTransferData( IndexedTransferable.ELEMENT_FLAVOR );
        if ( transferData instanceof IndexedTransferable.TupleContainer ) {
          IndexedTransferable.TupleContainer tc = (IndexedTransferable.TupleContainer) transferData;
          if ( tc.getSourceId().equals( targetList.getDragId() ) ) {
            return false;
          }
        }
        return true;
      } catch ( final Exception e ) {
        return false;
      }
    }
    return false;
  }

  public int getSourceActions( final JComponent c ) {
    if ( fieldPool ) {
      return TransferHandler.COPY;
    }
    return TransferHandler.MOVE;
  }

  protected Transferable createTransferable( final JComponent c ) {
    if ( c != targetList ) {
      throw new IllegalStateException();
    }

    final List<IndexedTransferable.FieldTuple> selectedValues = targetList.getSelectedFields();
    return new IndexedTransferable( targetList.getDragId(), selectedValues );
  }

  protected void exportDone( final JComponent source, final Transferable data, final int action ) {
    if ( ( action & TransferHandler.MOVE ) != TransferHandler.MOVE ) {
      return;
    }

    if ( source != targetList ) {
      throw new IllegalStateException();
    }
    try {
      final IndexedTransferable.TupleContainer items = extractFields( data );
      targetList.removeValues( Arrays.asList( items.getTuples() ) );
    } catch ( final Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
    }
  }
}
