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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.base;

import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;

/**
 * Creation-Date: 12.11.2006, 13:42:38
 *
 * @author Thomas Morgner
 */
public class AllPageFlowSelector implements PageFlowSelector {
  private boolean logicalPages;

  public AllPageFlowSelector( final boolean logicalPages ) {
    this.logicalPages = logicalPages;
  }

  public AllPageFlowSelector() {
    this( false );
  }

  public boolean isPhysicalPageAccepted( final PhysicalPageKey key ) {
    return logicalPages == false;
  }

  public boolean isLogicalPageAccepted( final LogicalPageKey key ) {
    return logicalPages;
  }
}
