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


package org.pentaho.reporting.designer.core.model.data;

import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;

import javax.swing.event.ChangeListener;

public interface DataSchemaManager {
  /**
   * The change listener is informed whenever a new model is available.
   *
   * @param l the change listener to be informed of model changes.
   */
  public void addChangeListener( ChangeListener l );

  public void removeChangeListener( ChangeListener l );

  public ContextAwareDataSchemaModel getModel();

  public void close();

}
