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

package org.pentaho.reporting.libraries.designtime.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class GenericTransferable implements Transferable {
  public static final DataFlavor ELEMENT_FLAVOR = new DataFlavor( DataFlavor.javaSerializedObjectMimeType +
    "; class=" + SerializedObjectContainer.class.getName(), // NON-NLS
    "Generic Local Transfer" ); // NON-NLS
  private SerializedObjectContainer value;

  public GenericTransferable( final Object value ) {
    if ( value == null ) {
      throw new NullPointerException();
    }
    this.value = new SerializedObjectContainer( new Object[] { value } );
  }

  public GenericTransferable( final Object[] data ) {
    if ( data == null ) {
      throw new NullPointerException();
    }
    this.value = new SerializedObjectContainer( data );
  }

  /**
   * Returns an array of DataFlavor objects indicating the flavors the data can be provided in.  The array should be
   * ordered according to preference for providing the data (from most richly descriptive to least descriptive).
   *
   * @return an array of data flavors in which this data can be transferred
   */
  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] { ELEMENT_FLAVOR };
  }

  /**
   * Returns whether or not the specified data flavor is supported for this object.
   *
   * @param flavor the requested flavor for the data
   * @return boolean indicating whether or not the data flavor is supported
   */
  public boolean isDataFlavorSupported( final DataFlavor flavor ) {
    return ELEMENT_FLAVOR.equals( flavor );
  }

  /**
   * Returns an object which represents the data to be transferred.  The class of the object returned is defined by the
   * representation class of the flavor.
   *
   * @param flavor the requested flavor for the data
   * @throws IOException                if the data is no longer available in the requested flavor.
   * @throws UnsupportedFlavorException if the requested data flavor is not supported.
   * @see DataFlavor#getRepresentationClass
   */
  public Object getTransferData( final DataFlavor flavor ) throws UnsupportedFlavorException, IOException {
    if ( isDataFlavorSupported( flavor ) == false ) {
      throw new UnsupportedFlavorException( flavor );
    }
    return value.getData();
  }
}
