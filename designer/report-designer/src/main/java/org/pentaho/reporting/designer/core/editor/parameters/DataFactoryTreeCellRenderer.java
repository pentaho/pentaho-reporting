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

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.beans.BeanInfo;
import java.util.Locale;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.04.2009 Time: 18:24:55
 *
 * @author Thomas Morgner.
 */
public class DataFactoryTreeCellRenderer extends DefaultTreeCellRenderer {
  public DataFactoryTreeCellRenderer() {
  }

  public Component getTreeCellRendererComponent( final JTree tree,
                                                 final Object value,
                                                 final boolean sel,
                                                 final boolean expanded,
                                                 final boolean leaf,
                                                 final int row,
                                                 final boolean hasFocus ) {
    super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
    setToolTipText( null );

    if ( value == tree.getModel().getRoot() ) {
      setText( "<ROOT> (should be invisible)" ); // NON-NLS
      return this;
    }

    if ( value instanceof DataFactoryWrapper ) {
      final DataFactoryWrapper wrapper = (DataFactoryWrapper) value;
      if ( wrapper.isRemoved() ) {
        throw new IllegalArgumentException( "Try to render a node that has been removed." );
      }
      final DataFactory dfac = wrapper.getEditedDataFactory();
      final DataFactoryMetaData data = dfac.getMetaData();

      final Image image = data.getIcon( Locale.getDefault(), BeanInfo.ICON_COLOR_32x32 );
      if ( image != null ) {
        setIcon( new ImageIcon( image ) );
      }

      final String connectionName = data.getDisplayConnectionName( dfac );
      if ( connectionName != null ) {
        setText( Messages.getString( "DataFactoryTreeCellRenderer.DataFactoryWithName",
          data.getDisplayName( Locale.getDefault() ), connectionName ) );
      } else {
        setText( Messages.getString( "DataFactoryTreeCellRenderer.DataFactoryWithoutName",
          data.getDisplayName( Locale.getDefault() ) ) );
      }
    }
    return this;
  }
}
