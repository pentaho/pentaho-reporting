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

package org.pentaho.reporting.engine.classic.core.designtime.datafactory;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

public class QueryAddAction extends AbstractAction {
  private NamedQueryModel model;

  public QueryAddAction( final NamedQueryModel model ) {
    this.model = model;
    final URL location =
        QueryAddAction.class
            .getResource( "/org/pentaho/reporting/engine/classic/core/designtime/datafactory/resources/Add.png" ); // NON-NLS
    if ( location != null ) {
      putValue( Action.SMALL_ICON, new ImageIcon( location ) );
    } else {
      putValue( Action.NAME, Messages.getInstance().getString( "AddQuery.Name" ) );
    }
    putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "AddQuery.Description" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    // Find a unique query name
    model.createQuery();
  }
}
