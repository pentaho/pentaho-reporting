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
