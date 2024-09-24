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

package org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model;

public interface QueryDialogModelListener<T> {
  void queryAdded( QueryDialogModelEvent<T> event );

  void queryRemoved( QueryDialogModelEvent<T> event );

  void queryUpdated( QueryDialogModelEvent<T> event );

  void queryDataChanged( QueryDialogModelEvent<T> event );

  void selectionChanged( QueryDialogModelEvent<T> event );

  void globalScriptChanged( QueryDialogModelEvent<T> event );
}
