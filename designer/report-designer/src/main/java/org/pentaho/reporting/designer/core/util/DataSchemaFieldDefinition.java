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

package org.pentaho.reporting.designer.core.util;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

import javax.swing.*;

public class DataSchemaFieldDefinition implements FieldDefinition {
  private String name;
  private String displayName;
  private Class type;
  private Icon icon;

  public DataSchemaFieldDefinition( final String name,
                                    final DataAttributes attributes,
                                    final DataAttributeContext dataAttributeContext ) {
    if ( attributes == null ) {
      throw new NullPointerException();
    }

    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( dataAttributeContext == null ) {
      throw new NullPointerException();
    }
    this.name = name;
    this.type = (Class) attributes.getMetaAttribute
      ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE, Class.class, dataAttributeContext );
    this.displayName = (String) attributes.getMetaAttribute
      ( MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.LABEL, String.class,
        dataAttributeContext );

    final Object source = attributes.getMetaAttribute
      ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.SOURCE, String.class,
        dataAttributeContext );
    if ( MetaAttributeNames.Core.SOURCE_VALUE_ENVIRONMENT.equals( source ) ) {
      icon = ( IconLoader.getInstance().getPropertiesDataSetIcon() );
    } else if ( MetaAttributeNames.Core.SOURCE_VALUE_EXPRESSION.equals( source ) ) {
      icon = ( IconLoader.getInstance().getFunctionIcon() );
    } else if ( MetaAttributeNames.Core.SOURCE_VALUE_PARAMETER.equals( source ) ) {
      icon = ( IconLoader.getInstance().getParameterIcon() );
    } else if ( MetaAttributeNames.Core.SOURCE_VALUE_TABLE.equals( source ) ) {
      icon = ( IconLoader.getInstance().getDataSetsIcon() );
    } else {
      icon = ( IconLoader.getInstance().getGenericSquare() );
    }
  }

  public String getName() {
    return name;
  }

  public String getDisplayName() {
    if ( displayName == null ) {
      return name;
    }
    return displayName;
  }

  public Class getFieldType() {
    if ( type != null ) {
      return type;
    }
    return Object.class;
  }

  public Icon getIcon() {
    return icon;
  }
}
