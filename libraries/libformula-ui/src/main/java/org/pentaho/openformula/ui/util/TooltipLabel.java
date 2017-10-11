/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
