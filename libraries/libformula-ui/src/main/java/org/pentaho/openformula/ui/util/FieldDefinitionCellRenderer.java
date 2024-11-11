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

import org.pentaho.openformula.ui.FieldDefinition;

import javax.swing.*;
import java.awt.*;

public class FieldDefinitionCellRenderer extends DefaultListCellRenderer {
  public FieldDefinitionCellRenderer() {
  }

  public Component getListCellRendererComponent( final JList list,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean cellHasFocus ) {
    super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    if ( value instanceof FieldDefinition ) {
      final FieldDefinition fd = (FieldDefinition) value;

      // format a display string
      final StringBuilder sb = new StringBuilder( 50 );
      // Add name
      sb.append( fd.getDisplayName() );
      sb.append( " (" ); // NON-NLS
      // Add Display name if it's not the same as Name
      if ( fd.getName().equals( fd.getDisplayName() ) == false ) {
        sb.append( fd.getName() ).append( ", " ); // NON-NLS
      }
      // Add Type
      sb.append( fd.getFieldType().getName() );
      //noinspection MagicCharacter
      sb.append( ')' );

      setText( sb.toString() );
      setIcon( fd.getIcon() );
    } else {
      setIcon( null );
      setText( " " );
    }
    return this;
  }
}
