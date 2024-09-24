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

package org.pentaho.reporting.designer.core;

import org.pentaho.reporting.designer.core.actions.global.ZoomAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;

import java.awt.event.ActionEvent;

class InternalZoomAction extends ZoomAction {
  InternalZoomAction( final int percentage ) {
    super( percentage );
  }

  public boolean isSelected() {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext != null ) {
      return activeContext.getZoomModel().getZoomAsPercentage() == ( getPercentage() / 100f );
    }
    return false;
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext != null ) {
      activeContext.getZoomModel().setZoomAsPercentage( getPercentage() / 100f );
    }
  }
}
