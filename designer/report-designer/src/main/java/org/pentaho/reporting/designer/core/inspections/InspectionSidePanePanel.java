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


package org.pentaho.reporting.designer.core.inspections;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.SidePanel;

import java.awt.*;

/**
 * This panel contains all inspections for the current report. Once fully implemented, there are at least two tables;
 * the auto-inspections table and the table for manually run inspections.
 *
 * @author Thomas Morgner
 */
public class InspectionSidePanePanel extends SidePanel {
  private InspectionsMessagePanel autoInspectionPanel;

  public InspectionSidePanePanel() {
    autoInspectionPanel = new InspectionsMessagePanel();
    setLayout( new BorderLayout() );
    add( autoInspectionPanel, BorderLayout.CENTER );
  }

  public void setEnabled( final boolean enabled ) {
    super.setEnabled( enabled );
    autoInspectionPanel.setEnabled( enabled );
  }

  protected void updateActiveContext( final ReportDocumentContext oldContext, final ReportDocumentContext newContext ) {
    if ( oldContext != null ) {
      oldContext.removeInspectionListener( autoInspectionPanel.getResultHandler() );
    }
    if ( newContext != null ) {
      newContext.addInspectionListener( autoInspectionPanel.getResultHandler() );
      setEnabled( true );
    } else {
      setEnabled( false );
    }

    autoInspectionPanel.setReportRenderContext( newContext );
  }
}
