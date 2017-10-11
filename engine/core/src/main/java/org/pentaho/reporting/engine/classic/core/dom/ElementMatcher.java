/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
