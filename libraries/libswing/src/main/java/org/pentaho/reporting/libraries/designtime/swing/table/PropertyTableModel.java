/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.designtime.swing.table;

import javax.swing.table.TableModel;
import java.beans.PropertyEditor;

public interface PropertyTableModel extends TableModel {
  Class getClassForCell( int row, int col );

  PropertyEditor getEditorForCell( int row, int column );
}
