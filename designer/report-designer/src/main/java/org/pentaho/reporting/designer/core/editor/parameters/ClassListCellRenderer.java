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

package org.pentaho.reporting.designer.core.editor.parameters;

import javax.swing.*;
import java.awt.*;

/**
 * Todo: Document me!
 * <p/>
 * Date: 14.05.2009 Time: 17:14:40
 *
 * @author Thomas Morgner.
 */
public class ClassListCellRenderer extends DefaultListCellRenderer {
  public ClassListCellRenderer() {
  }

  public Component getListCellRendererComponent( final JList list,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean cellHasFocus ) {
    if ( value instanceof Class == false ) {
      return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    }

    final String className = getSimpleName( (Class) value );
    return super.getListCellRendererComponent( list, className, index, isSelected, cellHasFocus );
  }

  public static String getSimpleName( final Class value ) {
    if ( java.sql.Date.class.equals( value ) ) {
      return Messages.getString( "ClassListCellRenderer.DateSQL" );
    }
    return value.getSimpleName();
  }
}
