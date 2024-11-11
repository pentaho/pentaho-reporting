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


package org.pentaho.openformula.ui.util;

import javax.swing.*;

public class TooltipLabel extends JLabel {
  /**
   * Creates a <code>JLabel</code> instance with no image and with an empty string for the title. The label is centered
   * vertically in its display area. The label's contents, once set, will be displayed on the leading edge of the
   * label's display area.
   *
   * @param description
   */
  public TooltipLabel( final String description ) {
    final ImageIcon imageIcon =
      new ImageIcon( getClass().getResource( "/org/pentaho/openformula/ui/images/InfoIcon.png" ) );
    setIcon( imageIcon );
    setToolTipText( description );
    // ensure that the actions are registered ...
  }
}
