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


package org.pentaho.reporting.designer.core.actions;

import org.pentaho.reporting.designer.core.ReportDesignerContext;

import javax.swing.*;

public abstract class AbstractDesignerContextAction extends AbstractAction implements DesignerContextAction {
  private ReportDesignerContext reportDesignerContext;

  /**
   * Defines an <code>Action</code> object with a default description string and default icon.
   */
  protected AbstractDesignerContextAction() {
    setEnabled( false );
  }

  public void setReportDesignerContext( final ReportDesignerContext context ) {
    final ReportDesignerContext old = this.reportDesignerContext;
    this.reportDesignerContext = context;
    updateDesignerContext( old, reportDesignerContext );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  public void setVisible( final boolean visible ) {
    putValue( "visible", visible );
  }

  public boolean isVisible() {
    final Object visibleRaw = getValue( "visible" );
    return visibleRaw == null || Boolean.TRUE.equals( visibleRaw );
  }

  protected void updateDesignerContext( final ReportDesignerContext oldContext,
                                        final ReportDesignerContext newContext ) {
    setEnabled( newContext != null );
  }

}
