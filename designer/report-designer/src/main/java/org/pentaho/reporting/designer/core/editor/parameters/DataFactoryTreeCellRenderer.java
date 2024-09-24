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
