/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.designtime.swing.bulk;

import javax.swing.*;

public class DefaultBulkListModel extends DefaultListModel implements BulkDataProvider {
  public DefaultBulkListModel() {
  }

  public int getBulkDataSize() {
    return getSize();
  }

  public Object[] getBulkData() {
    return toArray();
  }

  public void setBulkData( final Object[] data ) {
    clear();
    for ( int i = 0; i < data.length; i++ ) {
      final Object o = data[ i ];
      addElement( o );
    }
  }
}
