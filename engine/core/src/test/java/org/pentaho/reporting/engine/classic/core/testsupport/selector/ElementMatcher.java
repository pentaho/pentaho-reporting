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

import java.util.ArrayList;

public class ElementMatcher implements NodeMatcher {
  private String name;
  private ArrayList<NodeMatcher> attributeMatchers;

  public ElementMatcher( final Class name ) {
    this( name.getSimpleName() );
  }

  public ElementMatcher( final String name ) {
    this.attributeMatchers = new ArrayList<NodeMatcher>();
    this.name = name;
  }

  public void add( final NodeMatcher matchers ) {
    attributeMatchers.add( matchers );
  }

  public boolean matches( final RenderNode node ) {
    if ( node == null ) {
      throw new NullPointerException();
    }
    if ( node.getClass().getSimpleName().equals( name ) ) {
      return matchAttributes( node );
    }
    return false;
  }

  protected boolean matchAttributes( final RenderNode node ) {
    for ( int i = 0; i < attributeMatchers.size(); i++ ) {
      final NodeMatcher matcher = attributeMatchers.get( i );
      if ( matcher.matches( node ) == false ) {
        return false;
      }
    }
    return true;
  }

  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append( "ElementMatcher(" );
    stringBuilder.append( name );
    if ( attributeMatchers.isEmpty() == false ) {
      /*
       * stringBuilder.append("; Condition("); for (int i = 0; i < attributeMatchers.size(); i++) { final
       * AttributeMatcher matcher = attributeMatchers.get(i); if (i != 0) { stringBuilder.append(";"); }
       * stringBuilder.append(matcher); } stringBuilder.append(")");
       */
    }
    stringBuilder.append( ")" );
    return stringBuilder.toString();
  }
}
