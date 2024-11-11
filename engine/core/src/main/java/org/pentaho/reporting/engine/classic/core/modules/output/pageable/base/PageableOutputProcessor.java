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


package org.pentaho.reporting.engine.classic.core.modules.output.pageable.base;

import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;

/**
 * Creation-Date: 08.04.2007, 15:42:26
 *
 * @author Thomas Morgner
 */
public interface PageableOutputProcessor extends OutputProcessor {
  public int getPhysicalPageCount();

  public PhysicalPageKey getPhysicalPage( int page );

}
