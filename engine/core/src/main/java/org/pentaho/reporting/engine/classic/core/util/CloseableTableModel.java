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


package org.pentaho.reporting.engine.classic.core.util;

import javax.swing.table.TableModel;

/**
 * Extends the TableModel interface to be closeable. SQLResultSets need to be closed for instance.
 *
 * @author Thomas Morgner
 */
public interface CloseableTableModel extends TableModel {
  /**
   * If this model has disposeable resources assigned, close them or dispose them.
   */
  public void close();

}
