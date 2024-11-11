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

public class RenderNodeTypeMatcher implements NodeMatcher {
  private int nodeTypeMask;

  public RenderNodeTypeMatcher( final int nodeTypeMask ) {
    this.nodeTypeMask = nodeTypeMask;
  }

  public boolean matches( final RenderNode node ) {
    if ( eval( node ) ) {
      return true;
    }
    return false;
  }

  private boolean eval( final RenderNode node ) {
    if ( node.getNodeType() == nodeTypeMask ) {
      return true;
    }
    return false;
  }

  public String toString() {
    String prefix = "RenderNodeTypeMatcher(0x";
    prefix += Integer.toHexString( nodeTypeMask );
    return prefix + ")";
  }
}
