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

package org.pentaho.reporting.designer.core.editor.structuretree;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.beans.BeanInfo;
import java.util.Locale;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class StructureTreeCellRenderer extends DefaultTreeCellRenderer {
  public StructureTreeCellRenderer() {
  }

  private String formatElement( final Element element ) {
    final ElementMetaData data = element.getMetaData();
    final String displayName = data.getDisplayName( Locale.getDefault() );
    if ( WorkspaceSettings.getInstance().isElementsDisplayNames() ) {
      final String name = element.getName();

      if ( StringUtils.isEmpty( name ) ) {
        return ( displayName );
      } else {
        return Messages.getString( "StructureTreeCellRenderer.NamedElementMessage", displayName, name );
      }
    } else // values ..
    {
      final Object field = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
      if ( field != null ) {
        return Messages.getString( "StructureTreeCellRenderer.NamedElementMessage", displayName, field );
      }
      final Object value = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
      if ( value != null ) {
        return Messages.getString( "StructureTreeCellRenderer.NamedElementMessage", displayName, value );
      }
      final Object translationKey =
        element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.RESOURCE_IDENTIFIER );
      if ( translationKey != null ) {
        return Messages.getString( "StructureTreeCellRenderer.NamedElementMessage", displayName, translationKey );
      }
      final Object fields = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS );
      if ( fields instanceof String[] ) {
        final String[] fieldsArray = (String[]) fields;
        final StringBuilder b = new StringBuilder();
        for ( int i = 0; i < fieldsArray.length; i++ ) {
          final String f = fieldsArray[ i ];
          if ( i != 0 ) {
            b.append( ", " );
          }
          b.append( f );
        }
        return Messages.getString( "StructureTreeCellRenderer.NamedElementMessage", displayName, b.toString() );
      }
      return displayName;
    }
  }

  /**
   * Configures the renderer based on the passed in components. The value is set from messaging the tree with
   * <code>convertValueToText</code>, which ultimately invokes <code>toString</code> on <code>value</code>. The
   * foreground color is set based on the selection and the icon is set based on on leaf and expanded.
   */
  public Component getTreeCellRendererComponent( final JTree tree,
                                                 final Object value,
                                                 final boolean sel,
                                                 final boolean expanded,
                                                 final boolean leaf,
                                                 final int row,
                                                 final boolean hasFocus ) {
    super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
    setToolTipText( null );

    if ( value instanceof Element ) {
      final Element vr = (Element) value;
      final ElementMetaData data = vr.getMetaData();
      setText( formatElement( vr ) );
      final Image icon = data.getIcon( Locale.getDefault(), BeanInfo.ICON_COLOR_32x32 );
      if ( icon != null ) {
        setIcon( new ImageIcon( icon ) );
      }
      setToolTipText( data.getDescription( Locale.getDefault() ) );
    } else if ( value instanceof CompoundDataFactory ) {
      setText( Messages.getString( "StructureTreeCellRenderer.DataSets" ) );
      setIcon( IconLoader.getInstance().getDataSetsIcon() );
    } else if ( value instanceof ReportEnvironmentDataRow ) {
      setText( Messages.getString( "StructureTreeCellRenderer.Environment" ) );
      setIcon( IconLoader.getInstance().getPropertiesDataSetIcon() );
    } else if ( value instanceof ReportFunctionNode ) {
      setText( Messages.getString( "StructureTreeCellRenderer.Functions" ) );
      setIcon( IconLoader.getInstance().getFunctionsIcon() );
    } else if ( value instanceof ReportParametersNode || value instanceof SubReportParametersNode ) {
      setText( Messages.getString( "StructureTreeCellRenderer.Parameters" ) );
      setIcon( IconLoader.getInstance().getParameterIcon() );
    } else if ( value instanceof SubReportParametersNode.ExportParametersNode ) {
      setText( Messages.getString( "StructureTreeCellRenderer.ExportParameters" ) );
      setIcon( IconLoader.getInstance().getParameterIcon() );
    } else if ( value instanceof SubReportParametersNode.ImportParametersNode ) {
      setText( Messages.getString( "StructureTreeCellRenderer.ImportParameters" ) );
      setIcon( IconLoader.getInstance().getParameterIcon() );
    } else if ( value instanceof ParameterMapping ) {
      final ParameterMapping mapping = (ParameterMapping) value;
      setText( Messages
        .getString( "StructureTreeCellRenderer.ParameterMappingMessage", mapping.getAlias(), mapping.getName() ) );
      setIcon( IconLoader.getInstance().getParameterIcon() );
    } else if ( value instanceof ReportFieldNode ) {
      final ReportFieldNode fieldNode = (ReportFieldNode) value;
      final ContextAwareDataSchemaModel model = fieldNode.getDataSchemaModel();
      final DataAttributes attributes = model.getDataSchema().getAttributes( fieldNode.getFieldName() );
      setToolTipText( fieldNode.getFieldClass().getSimpleName() );
      if ( attributes == null ) {
        setText( fieldNode.toString() );
      } else {
        final String displayName = (String) attributes.getMetaAttribute
          ( MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.LABEL,
            String.class, model.getDataAttributeContext() );
        setText( formatFieldType( displayName, fieldNode.getFieldName(), fieldNode.getFieldClass() ) );
      }
    } else if ( value instanceof ReportQueryNode ) {
      final ReportQueryNode queryNode = (ReportQueryNode) value;
      setText( queryNode.getQueryName() );
      setToolTipText( queryNode.getDataFactory().getClass().getSimpleName() );
    } else if ( value instanceof Expression ) {
      final Expression expression = (Expression) value;
      if ( ExpressionRegistry.getInstance().isExpressionRegistered( expression.getClass().getName() ) == false ) {

        setText( expression.getClass().getName() );
      } else {
        final ExpressionMetaData expressionMetaData =
          ExpressionRegistry.getInstance().getExpressionMetaData( expression.getClass().getName() );

        if ( expression.getName() == null ) {
          setText( expressionMetaData.getDisplayName( Locale.getDefault() ) );
        } else {
          setText( Messages.getString( "StructureTreeCellRenderer.NamedExpressionMessage",
            expressionMetaData.getDisplayName( Locale.getDefault() ), expression.getName() ) );
        }
      }
    } else if ( value instanceof ParameterDefinitionEntry ) {
      final ParameterDefinitionEntry params = (ParameterDefinitionEntry) value;
      setText( params.getName() );
    } else if ( value instanceof DataFactory ) {
      final DataFactory dfac = (DataFactory) value;
      final DataFactoryMetaData data = dfac.getMetaData();

      final Image image = data.getIcon( Locale.getDefault(), BeanInfo.ICON_COLOR_32x32 );
      if ( image != null ) {
        setIcon( new ImageIcon( image ) );
      }

      final String connectionName = data.getDisplayConnectionName( dfac );
      if ( connectionName != null ) {
        setText( Messages.getString( "StructureTreeCellRenderer.NamedDataFactoryMessage",
          data.getDisplayName( Locale.getDefault() ), connectionName ) );
      } else {
        setText( data.getDisplayName( Locale.getDefault() ) );
      }
    } else if ( value instanceof ParentDataFactoryNode ) {
      setText( Messages.getString( "StructureTreeCellRenderer.InheritedDataFactories" ) );
    } else if ( value instanceof InheritedDataFactoryWrapper ) {
      final InheritedDataFactoryWrapper wrapper = (InheritedDataFactoryWrapper) value;
      final DataFactory dfac = wrapper.getDataFactory();
      if ( DataFactoryRegistry.getInstance().isRegistered( dfac.getClass().getName() ) == false ) {
        setText( dfac.getClass().getSimpleName() );
      } else {
        final DataFactoryMetaData data = dfac.getMetaData();

        final Image image = data.getIcon( Locale.getDefault(), BeanInfo.ICON_COLOR_32x32 );
        if ( image != null ) {
          setIcon( new ImageIcon( image ) );
        }

        final String connectionName = data.getDisplayConnectionName( dfac );
        if ( connectionName != null ) {
          setText( Messages.getString( "StructureTreeCellRenderer.NamedDataFactoryMessage",
            data.getDisplayName( Locale.getDefault() ), connectionName ) );
        } else {
          setText( data.getDisplayName( Locale.getDefault() ) );
        }
      }
    }
    return this;
  }

  private String formatFieldType( final String displayName,
                                  final String fieldName,
                                  final Class fieldClass ) {
    if ( displayName == null || ObjectUtilities.equal( displayName, fieldName ) ) {
      if ( fieldClass == null ) {
        return fieldName;
      }
      return Messages
        .getString( "StructureTreeCellRenderer.TypedElementMessage", fieldName, fieldClass.getSimpleName() );
    }

    if ( fieldClass == null ) {
      return Messages.getString( "StructureTreeCellRenderer.TypedElementMessage", displayName, fieldName );
    }
    return Messages.getString( "StructureTreeCellRenderer.TypedElementMessage",
      displayName, fieldName, fieldClass.getSimpleName() );
  }
}
