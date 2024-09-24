/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public final class OpenSampleReportAction extends AbstractDesignerContextAction {
  private File fileToOpen;

  public OpenSampleReportAction( final File fileToOpen,
                                 final String displayName ) {
    if ( fileToOpen == null ) {
      throw new NullPointerException();
    }
    this.fileToOpen = fileToOpen;
    putValue( Action.NAME, displayName );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    SwingUtilities.invokeLater( new OpenReportAction.OpenReportTask( fileToOpen, getReportDesignerContext() ) );
  }

}
