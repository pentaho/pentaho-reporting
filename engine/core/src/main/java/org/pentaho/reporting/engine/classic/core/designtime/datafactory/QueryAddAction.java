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
