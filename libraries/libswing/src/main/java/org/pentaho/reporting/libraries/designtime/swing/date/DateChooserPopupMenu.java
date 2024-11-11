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


package org.pentaho.reporting.libraries.designtime.swing.date;

import javax.swing.*;

public class DateChooserPopupMenu extends JPopupMenu {
  private DateChooserPanel dateChooserPanel;

  public DateChooserPopupMenu( final DateChooserPanel dateChooserPanel ) {
    this.dateChooserPanel = dateChooserPanel;
  }

  public void setVisible( final boolean b ) {
    final Boolean isCanceled = (Boolean) getClientProperty( "JPopupMenu.firePopupMenuCanceled" );
    if ( b ) {
      super.setVisible( true );
    } else if ( dateChooserPanel.isDateSelected() || Boolean.TRUE.equals( isCanceled ) ) {
      super.setVisible( false );
    }
  }
}
