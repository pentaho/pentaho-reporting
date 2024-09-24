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
