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


package org.pentaho.reporting.libraries.designtime.swing.event;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentChangeHandler implements DocumentListener {
  protected DocumentChangeHandler() {
  }

  protected abstract void handleChange( final DocumentEvent e );

  public void insertUpdate( final DocumentEvent e ) {
    handleChange( e );
  }

  public void removeUpdate( final DocumentEvent e ) {
    handleChange( e );
  }

  public void changedUpdate( final DocumentEvent e ) {
    handleChange( e );
  }
}
