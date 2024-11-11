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


package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;

public class MinorAxisLogicalPageContext extends MinorAxisNodeContext {
  private long pageWidth;

  public MinorAxisLogicalPageContext( final LogicalPageBox logicalPageBox ) {
    super( null );
    pageWidth = logicalPageBox.getPageWidth();
  }

  public MinorAxisNodeContext pop() {
    return null;
  }

  public long getResolvedPreferredSize() {
    return pageWidth;
  }

  public long getBlockContextWidth() {
    return pageWidth;
  }
}
