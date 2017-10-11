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

package org.pentaho.reporting.engine.classic.core.modules.gui.base;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ComponentListCellRenderer extends JComponent implements ListCellRenderer {
  private DefaultListCellRenderer defaultComp;
  private AbstractButton abstractButton;

  public ComponentListCellRenderer( final Class aComponentType ) {
    try {
      setLayout( new BorderLayout() );
      defaultComp = new DefaultListCellRenderer();
      abstractButton = (AbstractButton) aComponentType.newInstance();
      add( abstractButton );
    } catch ( Exception e ) {
      throw new IllegalStateException( "Unable to continue" + e );
    }
  }

  public Component getListCellRendererComponent( final JList list, final Object value, final int index,
      final boolean isSelected, final boolean cellHasFocus ) {
    defaultComp.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    abstractButton.setSelected( isSelected );
    abstractButton.setText( String.valueOf( value ) );
    abstractButton.setFont( new Font( Font.DIALOG, Font.PLAIN, 10 ) );
    abstractButton.setMargin( new Insets( 2, 0, 3, 0 ) );

    return this;
  }
}
