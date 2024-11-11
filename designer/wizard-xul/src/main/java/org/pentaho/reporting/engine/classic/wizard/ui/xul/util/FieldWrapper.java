/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.wizard.ui.xul.util;

import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.wizard.model.FieldDefinition;

public class FieldWrapper {
  private FieldDefinition fieldDefinition;
  private String displayName;

  public FieldWrapper( final FieldDefinition fieldDefinition,
                       final DataSchema dataSchema ) {
    if ( fieldDefinition == null ) {
      throw new NullPointerException();
    }

    this.fieldDefinition = fieldDefinition;

    final DataAttributes attributes = dataSchema.getAttributes( fieldDefinition.getField() );
    if ( attributes != null ) {
      final DefaultDataAttributeContext dataAttributeContext = new DefaultDataAttributeContext();
      displayName = (String) attributes.getMetaAttribute
        ( MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.LABEL,
          String.class, dataAttributeContext );
      if ( displayName != null ) {
        final Object indexColumn = attributes.getMetaAttribute
          ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.INDEXED_COLUMN, Boolean.class,
            dataAttributeContext );
        if ( Boolean.TRUE.equals( indexColumn ) ) {
          displayName += ( " (" + fieldDefinition.getField() + ")" );
        }
      }
    }

    if ( displayName == null ) {
      displayName = fieldDefinition.getField();
    }
  }

  public FieldDefinition getFieldDefinition() {
    return fieldDefinition;
  }

  public String getDisplayFieldName() {
    return displayName;
  }
}
