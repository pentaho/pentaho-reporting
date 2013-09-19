package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;

public class RepositoryTreeCellRenderer extends DefaultTreeCellRenderer
{
  public RepositoryTreeCellRenderer()
  {
  }

  public Component getTreeCellRendererComponent(final JTree tree,
                                                final Object value,
                                                final boolean sel,
                                                final boolean expanded,
                                                final boolean leaf,
                                                final int row,
                                                final boolean hasFocus)
  {
    final JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    if (value instanceof FileObject)
    {
      final FileObject node = (FileObject) value;
      try
      {
        if (leaf && (node.getType() == FileType.FOLDER))
        {
          component.setIcon(getOpenIcon());
        }
      }
      catch (FileSystemException fse)
      {
        // ignore exception here
      }
      component.setText(node.getName().getBaseName());
    }
    else
    {
      component.setText("/");
      component.setIcon(getOpenIcon());
    }
    return component;
  }
}
