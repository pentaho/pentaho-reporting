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


package org.pentaho.reporting.engine.classic.core.testsupport.selector;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

public class SkipResultMatcher implements NodeMatcher {
  private NodeMatcher matcher;

  public SkipResultMatcher( final NodeMatcher matcher ) {
    this.matcher = matcher;
  }

  public boolean matches( final RenderNode node ) {
    return matcher.matches( node );
  }
}
