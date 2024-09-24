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

package org.pentaho.reporting.engine.classic.core.layout.model;

public final class FlowPageBreakPositionList extends PageBreakPositionList {
  public FlowPageBreakPositionList() {
  }

  protected long getPageHeaderHeight( final long position ) {
    return 0;
  }
}
