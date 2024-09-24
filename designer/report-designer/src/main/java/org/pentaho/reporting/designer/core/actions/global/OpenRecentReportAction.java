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

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public final class OpenRecentReportAction extends AbstractDesignerContextAction {
  private File fileToOpen;

  public OpenRecentReportAction( final File fileToOpen ) {
    if ( fileToOpen == null ) {
      throw new NullPointerException();
    }
    this.fileToOpen = fileToOpen;
    putValue( Action.NAME, fileToOpen.getAbsolutePath() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    SwingUtilities.invokeLater( new OpenReportAction.OpenReportTask( fileToOpen, getReportDesignerContext() ) );
  }

}
