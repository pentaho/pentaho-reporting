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


package org.pentaho.reporting.engine.classic.wizard.ui.xul.util;

import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;

public class SourceFieldDefinition {
  private String displayName;
  private String fieldName;

  public SourceFieldDefinition( final String fieldName, final DataSchema dataSchema ) {
    if ( fieldName == null ) {
      throw new NullPointerException();
    }
    this.fieldName = fieldName;
    final DataAttributes attributes = dataSchema.getAttributes( fieldName );
    if ( attributes != null ) {
      final DefaultDataAttributeContext dataAttributeContext = new DefaultDataAttributeContext();
      displayName =
        (String) attributes.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
          MetaAttributeNames.Formatting.LABEL, String.class, dataAttributeContext );
      if ( displayName != null ) {
        final Object indexColumn =
          attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.INDEXED_COLUMN,
            Boolean.class, dataAttributeContext );
        if ( Boolean.TRUE.equals( indexColumn ) ) {
          displayName += ( " (" + fieldName + ")" );
        }
      }
    }
  }

  /**
   * Returns the formatted display name. This method is used via reflection by the Xul code.
   *
   * @return the display field name.
   * @noinspection UnusedDeclaration
   */
  public String getDisplayFieldName() {
    if ( displayName != null ) {
      return displayName;
    }

    return fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }

}
