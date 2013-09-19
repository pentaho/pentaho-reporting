package org.pentaho.reporting.designer.core.editor.fieldselector;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportFieldNode;
import org.pentaho.reporting.designer.core.model.ReportDataSchemaModel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.12.2009
 * Time: 16:06:12
 *
 * @author Thomas Morgner.
 */
public class FieldCellRenderer extends DefaultTableCellRenderer
{
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
    if (value instanceof ReportFieldNode == false)
    {
      return this;
    }
    final ReportFieldNode fieldNode = (ReportFieldNode) value;
    final ReportDataSchemaModel model = fieldNode.getDataSchemaModel();
    final DataAttributes attributes = model.getDataSchema().getAttributes(fieldNode.getFieldName());
    setToolTipText(fieldNode.getFieldClass().getSimpleName());
    if (attributes == null)
    {
      setText(fieldNode.toString());
      setIcon(IconLoader.getInstance().getBlankDocumentIcon());
    }
    else
    {
      final String displayName = (String) attributes.getMetaAttribute
          (MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.LABEL,
              String.class, model.getDataAttributeContext());
      setText(formatFieldType(displayName, fieldNode.getFieldName(), fieldNode.getFieldClass()));
      final Object source = attributes.getMetaAttribute
          (MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.SOURCE, String.class,
              model.getDataAttributeContext());
      if (MetaAttributeNames.Core.SOURCE_VALUE_ENVIRONMENT.equals(source))
      {
        setIcon(IconLoader.getInstance().getPropertiesDataSetIcon());
      }
      else if (MetaAttributeNames.Core.SOURCE_VALUE_EXPRESSION.equals(source))
      {
        setIcon(IconLoader.getInstance().getFunctionIcon());
      }
      else if (MetaAttributeNames.Core.SOURCE_VALUE_PARAMETER.equals(source))
      {
        setIcon(IconLoader.getInstance().getParameterIcon());
      }
      else if (MetaAttributeNames.Core.SOURCE_VALUE_TABLE.equals(source))
      {
        setIcon(IconLoader.getInstance().getDataSetsIcon());
      }
      else
      {
        setIcon(IconLoader.getInstance().getBlankDocumentIcon());
      }
    }


    return this;
  }


  private String formatFieldType(final String displayName,
                                 final String fieldName,
                                 final Class fieldClass)
  {
    if (displayName == null || ObjectUtilities.equal(displayName, fieldName))
    {
      if (fieldClass == null)
      {
        return fieldName;
      }
      return Messages.getString("FieldCellRenderer.TypedFieldMessage", fieldName , fieldClass.getSimpleName());
    }

    if (fieldClass == null)
    {
      return Messages.getString("FieldCellRenderer.TypedFieldMessage", displayName , fieldName);
    }
    return Messages.getString("FieldCellRenderer.AliasedTypedFieldMessage",
        displayName, fieldName , fieldClass.getSimpleName());
  }
}
