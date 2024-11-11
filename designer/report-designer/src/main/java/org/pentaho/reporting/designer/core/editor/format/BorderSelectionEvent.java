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


package org.pentaho.reporting.designer.core.editor.format;

import java.util.EventObject;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BorderSelectionEvent extends EventObject {
  private BorderSelection selection;

  public BorderSelectionEvent( final Object source, final BorderSelection selection ) {
    super( source );
    this.selection = selection;
  }

  public BorderSelection getSelection() {
    return selection;
  }
}
