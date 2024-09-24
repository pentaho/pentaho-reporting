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

package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document me!
 * <p/>
 * Date: 16.06.2009 Time: 19:06:16
 *
 * @author Thomas Morgner.
 */
public class SelectTabAction extends AbstractDesignerContextAction {
  private int tabIndex;

  public SelectTabAction( final int tabIndex, final String title ) {
    putValue( Action.NAME, title );

    if ( tabIndex < 0 ) {
      throw new IllegalArgumentException();
    }
    this.tabIndex = tabIndex;
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext designerContext = getReportDesignerContext();
    if ( designerContext == null ) {
      return;
    }
    if ( tabIndex >= designerContext.getReportRenderContextCount() ) {
      return;
    }
    designerContext.setActiveDocument( designerContext.getReportRenderContext( tabIndex ) );
  }
}
