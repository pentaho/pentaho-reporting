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


package org.pentaho.reporting.engine.classic.core.testsupport.selector;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class ElementTypeMatcher implements NodeMatcher {
  private String elementType;

  public ElementTypeMatcher( final String name ) {
    this.elementType = name;
  }

  public boolean matches( final RenderNode node ) {
    if ( eval( node ) ) {
      return true;
    }
    return false;
  }

  private boolean eval( final RenderNode node ) {
    return ObjectUtilities.equal( elementType, node.getElementType().getMetaData().getName() );
  }

  public String toString() {
    String prefix = "ElementTypeMatcher(";
    if ( elementType != null ) {
      prefix += "; " + elementType;
    }
    return prefix + ")";
  }
}
