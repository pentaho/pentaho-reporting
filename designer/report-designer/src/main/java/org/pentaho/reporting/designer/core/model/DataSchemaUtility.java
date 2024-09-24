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

package org.pentaho.reporting.designer.core.model;

import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

public final class DataSchemaUtility {
  private DataSchemaUtility() {
  }

  public static boolean isFiltered( final DataAttributes attributes, final DataAttributeContext context ) {
    if ( attributes == null ) {
      return true;
    }

    final Object o =
      attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.INDEXED_COLUMN,
        Boolean.class, context );
    if ( Boolean.TRUE.equals( o ) ) {
      if ( WorkspaceSettings.getInstance().isShowIndexColumns() ) {
        return false;
      }
      return true;
    }
    return false;
  }
}
