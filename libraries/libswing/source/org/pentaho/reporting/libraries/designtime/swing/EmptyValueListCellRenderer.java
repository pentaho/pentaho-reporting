package org.pentaho.reporting.libraries.designtime.swing;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * A list cell renderer that corrects the obvious Swing bug where empty strings or null-values as
 * first element in a JList or a JCombobox cause the list to have a size near to zero. This bug prevents
 * any meaningful interaction with the List/Combobox.
 *
 * @author Thomas Morgner.
 */
public class EmptyValueListCellRenderer extends DefaultListCellRenderer
{
  public EmptyValueListCellRenderer()
  {
  }

  public Component getListCellRendererComponent(final JList list,
                                                final Object value,
                                                final int index,
                                                final boolean isSelected,
                                                final boolean cellHasFocus)
  {
    if (value == null || "".equals(value))
    {
      return super.getListCellRendererComponent(list, " ", index, isSelected, cellHasFocus);
    }
    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
  }
}
