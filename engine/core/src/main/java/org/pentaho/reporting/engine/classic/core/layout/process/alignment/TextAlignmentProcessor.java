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


package org.pentaho.reporting.engine.classic.core.layout.process.alignment;

import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SequenceList;

/**
 * A general alignment processor.
 *
 * @author Thomas Morgner
 */
public interface TextAlignmentProcessor {
  public void initialize( OutputProcessorMetaData metaData, SequenceList sequence, long start, long end,
      PageGrid breaks, boolean overflowX );

  public void updateLineSize( final long start, final long end );

  public void deinitialize();

  public boolean hasNext();

  public RenderBox next();

}
