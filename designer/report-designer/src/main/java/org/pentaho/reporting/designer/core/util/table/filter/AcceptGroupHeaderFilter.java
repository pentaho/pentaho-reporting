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

import org.pentaho.reporting.designer.core.util.table.GroupingHeader;

public class AcceptGroupHeaderFilter implements Filter {
  public AcceptGroupHeaderFilter() {
  }

  public Result isMatch( final Object o ) {
    if ( o instanceof GroupingHeader ) {
      return Result.ACCEPT;
    }
    return Result.UNDECIDED;
  }
}
