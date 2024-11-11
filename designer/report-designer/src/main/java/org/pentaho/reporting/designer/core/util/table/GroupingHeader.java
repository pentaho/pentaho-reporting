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


package org.pentaho.reporting.designer.core.util.table;

import java.io.Serializable;

public class GroupingHeader implements Serializable {
  private String headerText;
  private boolean collapsed;

  public GroupingHeader( final String headerText ) {
    this.headerText = headerText;
  }

  public String getHeaderText() {
    return headerText;
  }

  public String toString() {
    return headerText;
  }

  public boolean isCollapsed() {
    return collapsed;
  }

  public void setCollapsed( final boolean collapsed ) {
    this.collapsed = collapsed;
  }
}
