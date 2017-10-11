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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class IndexedTransferable implements Transferable {
  public static class FieldTuple implements Serializable {
    private int index;
    private String value;
    private String title;
    private Object raw;

    public FieldTuple( final int index, final String value ) {
      this.index = index;
      this.value = value;
    }

    public FieldTuple( final int index, final String value, final String title, final Object raw ) {
      this( index, value );
      this.title = title;
      this.raw = raw;
    }

    public String getTitle() {
      return title;
    }

    public Object getRaw() {
      return raw;
    }

    public int getIndex() {
      return index;
    }

    public String getValue() {
      return value;
    }
  }

  public static class TupleContainer implements Serializable {
    private FieldTuple[] tuples;
    private UUID sourceId;

    private TupleContainer( final UUID sourceId,
                            final FieldTuple[] tuples ) {
      this.sourceId = sourceId;
      this.tuples = tuples;
    }

    public FieldTuple[] getTuples() {
      return tuples.clone();
    }

    public UUID getSourceId() {
      return sourceId;
    }
  }

  public static final DataFlavor ELEMENT_FLAVOR = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType +
    "; class=" + TupleContainer.class.getName(), // NON-NLS
    "Indexed Field List Transfer" // NON-NLS
  );

  public static final IndexedTransferable.TupleContainer EMPTY =
    new IndexedTransferable.TupleContainer( new UUID( 0, 0 ), new FieldTuple[ 0 ] );

  private TupleContainer tupleContainer;

  public IndexedTransferable( final UUID sourceId, final List<FieldTuple> tuples ) {
    this.tupleContainer = new TupleContainer( sourceId, tuples.toArray( new FieldTuple[ tuples.size() ] ) );
  }

  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] { ELEMENT_FLAVOR };
  }

  public boolean isDataFlavorSupported( final DataFlavor flavor ) {
    return ELEMENT_FLAVOR.equals( flavor );
  }

  public Object getTransferData( final DataFlavor flavor ) throws UnsupportedFlavorException, IOException {
    if ( isDataFlavorSupported( flavor ) ) {
      return tupleContainer;
    } else {
      throw new UnsupportedFlavorException( flavor );
    }
  }
}
