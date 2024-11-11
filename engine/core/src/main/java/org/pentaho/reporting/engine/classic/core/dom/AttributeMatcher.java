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
