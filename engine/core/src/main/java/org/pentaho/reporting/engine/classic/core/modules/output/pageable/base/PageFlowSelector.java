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

import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;

/**
 * Creation-Date: 09.04.2007, 11:02:04
 *
 * @author Thomas Morgner
 */
public interface PageFlowSelector extends FlowSelector {
  public boolean isPhysicalPageAccepted( PhysicalPageKey key );
}
