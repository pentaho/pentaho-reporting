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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.actions;

import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.AbstractActionPlugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Creation-Date: 15.08.2007, 16:12:34
 *
 * @author Thomas Morgner
 */
public class PaginatedUpdateListener implements PropertyChangeListener {
  private AbstractActionPlugin actionPlugin;

  public PaginatedUpdateListener( final AbstractActionPlugin actionPlugin ) {
    if ( actionPlugin == null ) {
      throw new NullPointerException();
    }
    this.actionPlugin = actionPlugin;
  }

  public void propertyChange( final PropertyChangeEvent evt ) {
    if ( PreviewPane.PAGINATED_PROPERTY.equals( evt.getPropertyName() ) == false ) {
      return;
    }

    actionPlugin.setEnabled( Boolean.TRUE.equals( evt.getNewValue() ) );
  }
}
