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

package org.pentaho.reporting.designer.core.editor.fieldselector;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportFieldNode;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.12.2009 Time: 16:06:12
 *
 * @author Thomas Morgner.
 */
public class FieldCellRenderer extends DefaultTableCellRenderer {
  /**
   * Returns the default table cell renderer.
   * <p/>
   * During a printing operation, this method will be called with <code>isSelected</code> and <code>hasFocus</code>
   * values of <code>false</code> to prevent selection and focus from appearing in the printed output. To do other
   * customization based on whether or not the table is being printed, check the return value from {@link
   * javax.swing.JComponent#isPaintingForPrint()}.
   *
   * @param table      the <code>JTable</code>
   * @param value      the value to assign to the cell at <code>[row, column]</code>
   * @param isSelected true if cell is selected
   * @param hasFocus   true if cell has focus
   * @param row        the row of the cell to render
   * @param column     the column of the cell to render
   * @return the default table cell renderer
   * @see javax.swing.JComponent#isPaintingForPrint()
   */
  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {
    if ( value instanceof ReportFieldNode == false ) {
      return this;
    }
    final ReportFieldNode fieldNode = (ReportFieldNode) value;
    final ContextAwareDataSchemaModel model = fieldNode.getDataSchemaModel();
    final DataAttributes attributes = model.getDataSchema().getAttributes( fieldNode.getFieldName() );
    setToolTipText( fieldNode.getFieldClass().getSimpleName() );
    if ( attributes == null ) {
      setText( fieldNode.toString() );
      setIcon( IconLoader.getInstance().getBlankDocumentIcon() );
    } else {
      configureFieldText( fieldNode, model, attributes );
      configureFieldIcon( model, attributes );
    }
    return this;
  }

  protected void configureFieldText( final ReportFieldNode fieldNode,
                                     final ContextAwareDataSchemaModel model,
                                     final DataAttributes attributes ) {
    final String displayName = (String) attributes.getMetaAttribute
      ( MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.LABEL,
        String.class, model.getDataAttributeContext() );
    setText( formatFieldType( displayName, fieldNode.getFieldName(), fieldNode.getFieldClass() ) );
  }

  protected void configureFieldIcon( final ContextAwareDataSchemaModel model,
                                     final DataAttributes attributes ) {
    final Object source = attributes.getMetaAttribute
      ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.SOURCE, String.class,
        model.getDataAttributeContext() );
    if ( MetaAttributeNames.Core.SOURCE_VALUE_ENVIRONMENT.equals( source ) ) {
      setIcon( IconLoader.getInstance().getPropertiesDataSetIcon() );
    } else if ( MetaAttributeNames.Core.SOURCE_VALUE_EXPRESSION.equals( source ) ) {
      setIcon( IconLoader.getInstance().getFunctionIcon() );
    } else if ( MetaAttributeNames.Core.SOURCE_VALUE_PARAMETER.equals( source ) ) {
      setIcon( IconLoader.getInstance().getParameterIcon() );
    } else if ( MetaAttributeNames.Core.SOURCE_VALUE_TABLE.equals( source ) ) {
      setIcon( IconLoader.getInstance().getDataSetsIcon() );
    } else {
      setIcon( IconLoader.getInstance().getBlankDocumentIcon() );
    }
  }

  private String formatFieldType( final String displayName,
                                  final String fieldName,
                                  final Class fieldClass ) {
    if ( displayName == null || ObjectUtilities.equal( displayName, fieldName ) ) {
      if ( fieldClass == null ) {
        return fieldName;
      }
      return Messages.getString( "FieldCellRenderer.TypedFieldMessage", fieldName, fieldClass.getSimpleName() );
    }

    if ( fieldClass == null ) {
      return Messages.getString( "FieldCellRenderer.TypedFieldMessage", displayName, fieldName );
    }
    return Messages.getString( "FieldCellRenderer.AliasedTypedFieldMessage",
      displayName, fieldName, fieldClass.getSimpleName() );
  }
}
