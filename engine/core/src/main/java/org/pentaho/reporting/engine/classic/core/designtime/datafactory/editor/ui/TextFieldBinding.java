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


package org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.ui;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;

import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;

public abstract class TextFieldBinding extends DocumentChangeHandler implements Runnable {
  private boolean executed;

  protected void handleChange( final DocumentEvent documentEvent ) {
    executed = false;
    SwingUtilities.invokeLater( this );
  }

  public final void run() {
    if ( executed ) {
      return;
    }

    performUpdate();
    executed = true;
  }

  protected abstract void performUpdate();
}
