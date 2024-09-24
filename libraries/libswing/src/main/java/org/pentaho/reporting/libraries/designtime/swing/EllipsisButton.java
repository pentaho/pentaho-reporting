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

package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.*;
import java.awt.*;


public class EllipsisButton extends JButton {
  /**
   * Creates a button with no set text or icon.
   */
  public EllipsisButton() {
  }

  /**
   * Creates a button with an icon.
   *
   * @param icon the Icon image to display on the button
   */
  public EllipsisButton( final Icon icon ) {
    super( icon );
  }

  /**
   * Creates a button with text.
   *
   * @param text the text of the button
   */
  public EllipsisButton( final String text ) {
    super( text );
  }

  /**
   * Creates a button where properties are taken from the <code>Action</code> supplied.
   *
   * @param a the <code>Action</code> used to specify the new button
   * @since 1.3
   */
  public EllipsisButton( final Action a ) {
    super( a );
  }

  /**
   * Creates a button with initial text and an icon.
   *
   * @param text the text of the button
   * @param icon the Icon image to display on the button
   */
  public EllipsisButton( final String text, final Icon icon ) {
    super( text, icon );
  }

  /**
   * Resets the UI property to a value from the current look and feel.
   *
   * @see javax.swing.JComponent#updateUI
   */
  public void updateUI() {
    super.updateUI();

    setDefaultCapable( false );
    setMargin( new Insets( 0, 0, 0, 0 ) );

    try {
      //noinspection AccessOfSystemProperties
      final String lcOSName = System.getProperty( "os.name" ).toLowerCase();
      final boolean MAC_OS_X = lcOSName.startsWith( "mac os x" );
      if ( MAC_OS_X ) {
        setPreferredSize( new Dimension( 30, 16 ) );
      }
    } catch ( Exception e ) {
      // contain the madness. Apple is just strange sometimes ..
    }
  }
}
