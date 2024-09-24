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

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public final class RunAsPopupAction extends AbstractReportContextAction {
  public RunAsPopupAction() {
    putValue( Action.NAME, ActionMessages.getString( "RunAsPopupAction.Text" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getCreateReportIcon() );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "RunAsPopupAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "RunAsPopupAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "RunAsPopupAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final JPopupMenu menu = getReportDesignerContext().getPopupMenu( "popup-RunAs" ); // $NON-NLS$
    final Object source = e.getSource();
    if ( source instanceof Component ) {
      final Component c = (Component) source;
      menu.show( c, 0, c.getHeight() );
    } else {
      final Component parent = getReportDesignerContext().getView().getParent();
      menu.show( parent, 0, 0 );
    }
  }
}
