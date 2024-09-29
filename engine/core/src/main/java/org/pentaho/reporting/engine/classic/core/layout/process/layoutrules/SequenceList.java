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


package org.pentaho.reporting.engine.classic.core.layout.process.layoutrules;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

public interface SequenceList {
  RenderNode getNode( int index );

  InlineSequenceElement getSequenceElement( int index );

  long getMinimumLength( int index );

  void clear();

  void add( InlineSequenceElement element, RenderNode node );

  int size();

  InlineSequenceElement[] getSequenceElements( InlineSequenceElement[] target );

  RenderNode[] getNodes( RenderNode[] target );
}
