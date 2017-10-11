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
