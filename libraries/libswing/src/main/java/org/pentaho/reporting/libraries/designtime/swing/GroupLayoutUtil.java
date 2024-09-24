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
