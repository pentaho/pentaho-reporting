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

import org.pentaho.reporting.engine.classic.core.ReportElement;

public class AttributeMatcher implements NodeMatcher {
  public String namespace;
  public String name;
  public Object value;

  public AttributeMatcher( final String name ) {
    this( null, name, null );
  }

  public AttributeMatcher( final String namespace, final String name ) {
    this( namespace, name, null );
  }

  public AttributeMatcher( final String namespace, final String name, final Object value ) {
    this.namespace = namespace;
    this.name = name;
    this.value = value;
  }

  public boolean matches( final MatcherContext context, final ReportElement node ) {
    if ( eval( node ) ) {
      return true;
    }
    return false;
  }

  private boolean eval( final ReportElement node ) {
    if ( namespace == null ) {
      final Object firstAttribute = node.getAttributes().getFirstAttribute( name );
      if ( value != null ) {
        return value.equals( firstAttribute );
      }
      return firstAttribute != null;
    }

    final Object attribute = node.getAttributes().getAttribute( namespace, name );
    if ( value != null ) {
      return value.equals( attribute );
    }
    return attribute != null;
  }

  public String toString() {
    String prefix = "AttributeMatcher(";
    if ( namespace != null ) {
      prefix += "'" + namespace + "'; ";
    }
    prefix += name;
    if ( value != null ) {
      prefix += "; " + value;
    }
    return prefix + ")";
  }
}
