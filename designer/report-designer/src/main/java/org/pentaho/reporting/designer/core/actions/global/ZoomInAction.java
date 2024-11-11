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


package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ZoomModel;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.PreviewPaneUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ZoomInAction extends AbstractReportContextAction {
  private static double[] ZOOM_FACTORS = { 0.50f, 0.75f, 1.00f, 1.25f, 1.50f, 2.00f, 4.00f };

  public ZoomInAction() {
    putValue( Action.NAME, ActionMessages.getString( "ZoomInAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ZoomInAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ZoomInAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ZoomInAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }
    final ZoomModel zoomModel = activeContext.getZoomModel();

    final double nextZoomIn = PreviewPaneUtilities.getNextZoomIn( zoomModel.getZoomAsPercentage(), ZOOM_FACTORS );
    if ( nextZoomIn == 0 ) {
      return;
    }
    zoomModel.setZoomAsPercentage( (float) nextZoomIn );
  }
}
