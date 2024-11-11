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

public class GroupLayoutUtil {
  private GroupLayoutUtil() {
  }

  public static JPanel makeSimpleForm( final int rows, final int cols, final Component... components ) {
    final JPanel panel = new JPanel();
    final GroupLayout layout = new GroupLayout( panel );
    panel.setLayout( layout );

    final GroupLayout.SequentialGroup horizontalGroup = layout.createSequentialGroup();
    for ( int c = 0; c < cols; c += 1 ) {
      final GroupLayout.ParallelGroup parallelGroup = layout.createParallelGroup( GroupLayout.Alignment.LEADING );
      for ( int r = 0; r < rows; r += 1 ) {
        final int index = r * cols + c;
        parallelGroup.addComponent( components[ index ] );
      }
      horizontalGroup.addGroup( parallelGroup );
    }
    layout.setHorizontalGroup( horizontalGroup );

    final GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
    for ( int r = 0; r < rows; r += 1 ) {
      final GroupLayout.ParallelGroup parallelGroup = layout.createParallelGroup( GroupLayout.Alignment.LEADING );
      for ( int c = 0; c < cols; c += 1 ) {
        final int index = r * cols + c;
        parallelGroup.addComponent( components[ index ] );
      }
      verticalGroup.addGroup( parallelGroup );
    }
    layout.setVerticalGroup( verticalGroup );
    return panel;
  }
}
