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

package org.pentaho.openformula.ui.util;

import org.pentaho.reporting.libraries.formula.function.FunctionDescription;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class FunctionDescriptionCellRenderer extends DefaultListCellRenderer {
  public FunctionDescriptionCellRenderer() {
  }

  public Component getListCellRendererComponent( final JList list,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean cellHasFocus ) {
    super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    if ( value instanceof FunctionDescription ) {
      final FunctionDescription cat = (FunctionDescription) value;
      setText( cat.getDisplayName( Locale.getDefault() ) );
      setToolTipText( cat.getDescription( Locale.getDefault() ) );
    } else {
      setText( " " ); // NON-NLS
      setToolTipText( null );
    }
    return this;
  }
}
