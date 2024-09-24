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

package org.pentaho.reporting.designer.core.util.table.filter;

import org.pentaho.reporting.designer.core.util.table.GroupedName;
import org.pentaho.reporting.engine.classic.core.metadata.MetaData;

public class RejectStandaloneExpressionPropertiesFilter implements Filter {
  public RejectStandaloneExpressionPropertiesFilter() {
  }

  public Result isMatch( final Object o ) {
    if ( o instanceof GroupedName ) {
      final GroupedName name = (GroupedName) o;
      final MetaData metaData = name.getMetaData();
      if ( "name".equals( metaData.getName() ) ) {
        return Result.REJECT;
      }
      if ( "dependencyLevel".equals( metaData.getName() ) ) {
        return Result.REJECT;
      }
    }
    return Result.UNDECIDED;
  }
}
