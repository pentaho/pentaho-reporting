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


package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import java.awt.Component;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class RepositoryTreeCellRenderer extends DefaultTreeCellRenderer {
  public RepositoryTreeCellRenderer() {
  }

  public Component getTreeCellRendererComponent( final JTree tree,
                                                 final Object value,
                                                 final boolean sel,
                                                 final boolean expanded,
                                                 final boolean leaf,
                                                 final int row,
                                                 final boolean hasFocus ) {
    final JLabel component =
      (JLabel) super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
    if ( value instanceof FileObject ) {
      final FileObject node = (FileObject) value;
      try {
        if ( leaf && ( node.getType() == FileType.FOLDER ) ) {
          component.setIcon( getOpenIcon() );
        }
      } catch ( FileSystemException fse ) {
        // ignore exception here
      }
      component.setText( node.getName().getBaseName() );
    } else {
      component.setText( "/" );
      component.setIcon( getOpenIcon() );
    }
    return component;
  }
}
