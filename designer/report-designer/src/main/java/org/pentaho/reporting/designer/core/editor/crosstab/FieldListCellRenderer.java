/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.crosstab;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.FixDefaultListCellRenderer;

import javax.swing.*;
import java.awt.*;

public class FieldListCellRenderer extends FixDefaultListCellRenderer {
  private ContextAwareDataSchemaModel model;

  public FieldListCellRenderer() {
  }

  public void setModel( final ContextAwareDataSchemaModel model ) {
    this.model = model;
  }

  public ContextAwareDataSchemaModel getModel() {
    return model;
  }

  public Component getListCellRendererComponent( final JList list,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean cellHasFocus ) {
    super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

    setToolTipText( null );
    setIcon( IconLoader.getInstance().getEmptyIcon() );

    if ( model != null && value instanceof String ) {
      String field = (String) value;
      final DataAttributes attributes = model.getDataSchema().getAttributes( field );
      final DataAttributeContext dac = model.getDataAttributeContext();
      if ( attributes != null ) {
        Class fieldClass = configureFieldType( attributes, dac );
        configureFieldText( field, fieldClass, attributes, dac );
        configureFieldIcon( attributes, dac );
      } else {
        setText( field );
      }
    }
    return this;
  }

  protected void configureFieldText( final String fieldName, final Class fieldClass,
                                     final DataAttributes attributes, final DataAttributeContext dac ) {
    final String displayName = (String) attributes.getMetaAttribute
      ( MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.LABEL, String.class, dac );
    setText( formatFieldType( displayName, fieldName, fieldClass ) );
  }

  protected Class configureFieldType( final DataAttributes attributes, final DataAttributeContext dac ) {
    Object metaAttribute =
      attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE, Class.class, dac );
    if ( metaAttribute instanceof Class ) {
      Class fieldClass = (Class) metaAttribute;
      setToolTipText( fieldClass.getSimpleName() );
      return fieldClass;
    }
    return Object.class;
  }

  protected void configureFieldIcon( final DataAttributes attributes, final DataAttributeContext dac ) {
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
