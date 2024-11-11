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


package org.pentaho.reporting.engine.classic.core.dom;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;

public class ElementMatcher implements NodeMatcher {
  private String name;
  private ArrayList<AttributeMatcher> attributeMatchers;

  public ElementMatcher( final ElementType name ) {
    this( name.getMetaData().getName() );
  }

  public ElementMatcher( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    this.attributeMatchers = new ArrayList<AttributeMatcher>();
    this.name = name;
  }

  public void add( final AttributeMatcher matchers ) {
    attributeMatchers.add( matchers );
  }

  public boolean matches( final MatcherContext context, final ReportElement node ) {
    if ( node == null ) {
      throw new NullPointerException();
    }
    final String type = node.getElementType().getMetaData().getName();
    if ( type.equals( name ) ) {
      return matchAttributes( context, node );
    }
    return false;
  }

  protected boolean matchAttributes( final MatcherContext context, final ReportElement node ) {
    for ( int i = 0; i < attributeMatchers.size(); i++ ) {
      final AttributeMatcher matcher = attributeMatchers.get( i );
      if ( matcher.matches( context, node ) == false ) {
        return false;
      }
    }
    return true;
  }

  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append( "ElementMatcher(" );
    stringBuilder.append( name );
    /*
     * if ( attributeMatchers.isEmpty() == false ) { stringBuilder.append("; Condition("); for (int i = 0; i <
     * attributeMatchers.size(); i++) { final AttributeMatcher matcher = attributeMatchers.get(i); if (i != 0) {
     * stringBuilder.append(";"); } stringBuilder.append(matcher); } stringBuilder.append(")"); }
     */
    stringBuilder.append( ")" );
    return stringBuilder.toString();
  }
}
