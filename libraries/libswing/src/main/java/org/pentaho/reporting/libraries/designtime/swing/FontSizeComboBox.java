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


package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.*;
import java.awt.*;

/**
 * A preconfigured combobox for font-sizes.
 *
 * @author Thomas Morgner
 */
public final class FontSizeComboBox extends JComboBox {
  public FontSizeComboBox() {
    final Integer[] fontSizes = new Integer[] {
      new Integer( 6 ),
      new Integer( 7 ),
      new Integer( 8 ),
      new Integer( 9 ),
      new Integer( 10 ),
      new Integer( 11 ),
      new Integer( 12 ),
      new Integer( 14 ),
      new Integer( 16 ),
      new Integer( 18 ),
      new Integer( 20 ),
      new Integer( 24 ),
      new Integer( 28 ),
      new Integer( 32 ),
      new Integer( 36 ),
      new Integer( 48 ),
      new Integer( 72 ) };
    setModel( new DefaultComboBoxModel( fontSizes ) );
    setFocusable( false );
    final int height1 = getPreferredSize().height;
    setMaximumSize( new Dimension( height1 * 2, height1 ) );
  }

  /**
   * Updates the selected value without fireing an ActionEvent.
   *
   * @param o the new selected value.
   */
  protected void setValueFromModel( final Object o ) {
    final Action action = getAction();
    setAction( null );
    setSelectedItem( o );
    setAction( action );
  }

}
