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


package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;

/**
 * Creation-Date: 02.05.2007, 15:36:53
 *
 * @author Thomas Morgner
 */
public interface IterativeOutputProcessor extends OutputProcessor {
  public void processIterativeContent( LogicalPageBox logicalPageBox, boolean performOutput )
    throws ContentProcessingException;
}
