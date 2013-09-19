package org.pentaho.reporting.designer.core.util.table;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.pentaho.reporting.engine.classic.core.metadata.MetaData;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.12.2009
 * Time: 16:43:41
 *
 * @author Thomas Morgner.
 */
public class GroupedNameCellRenderer extends DefaultTableCellRenderer
{
  public GroupedNameCellRenderer()
  {
  }

  /**
   * Returns the default table cell renderer.
   * <p/>
   * During a printing operation, this method will be called with
   * <code>isSelected</code> and <code>hasFocus</code> values of
   * <code>false</code> to prevent selection and focus from appearing
   * in the printed output. To do other customization based on whether
   * or not the table is being printed, check the return value from
   * {@link javax.swing.JComponent#isPaintingForPrint()}.
   *
   * @param table      the <code>JTable</code>
   * @param value      the value to assign to the cell at
   *                   <code>[row, column]</code>
   * @param isSelected true if cell is selected
   * @param hasFocus   true if cell has focus
   * @param row        the row of the cell to render
   * @param column     the column of the cell to render
   * @return the default table cell renderer
   * @see javax.swing.JComponent#isPaintingForPrint()
   */
  public Component getTableCellRendererComponent(final JTable table,
                                                 final Object value,
                                                 final boolean isSelected,
                                                 final boolean hasFocus,
                                                 final int row,
                                                 final int column)
  {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (value instanceof GroupedName)
    {
      final GroupedName name = (GroupedName) value;
      final String displayName = name.getName();
      final MetaData metaData = name.getMetaData();
      final boolean deprecated;
      final boolean expert;
      final boolean preferred;
      if (metaData != null)
      {
        deprecated = metaData.isDeprecated();
        expert = metaData.isExpert();
        preferred = metaData.isPreferred();
      }
      else
      {
        deprecated = false;
        expert = false;
        preferred = false;
      }
      String prefix = "";
      if (deprecated)
      {
        prefix = "*";
      }
      int fontStyle = Font.PLAIN;
      if (expert)
      {
        fontStyle |= Font.ITALIC;
      }
      if (preferred)
      {
        fontStyle |= Font.BOLD;
      }
      setFont(getFont().deriveFont(fontStyle));
      if (table.getModel() instanceof SortableTableModel)
      {
        final SortableTableModel model = (SortableTableModel) table.getModel();
        final TableStyle style = model.getTableStyle();
        if (TableStyle.GROUPED.equals(style))
        {
          setText(prefix + displayName);
        }
        else
        {
          setText(prefix + displayName + " (" + name.getGroupName() + ")");
        }
      }
      else
      {
        setText(prefix + displayName + " (" + name.getGroupName() + ")");
      }
    }
    return this;
  }
}
