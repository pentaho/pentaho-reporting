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

public class AttributeMatcher implements NodeMatcher {
  public String namespace;
  public String name;
  public Object value;

  public AttributeMatcher( final String name ) {
    this( null, name, null );
  }

  public AttributeMatcher( final String namespace, final String name ) {
    this.namespace = namespace;
    this.name = name;
  }

  public AttributeMatcher( final String namespace, final String name, final Object value ) {
    this.namespace = namespace;
    this.name = name;
    this.value = value;
  }

  private boolean equalString( final String s1, final String s2 ) {
    // noinspection StringEquality
    if ( s1 == s2 ) {
      return true;
    }
    if ( s1 != null ) {
      return s1.equals( s2 );
    }
    return false;
  }

  public boolean matches( final RenderNode node ) {
    if ( eval( node ) ) {
      return true;
    }
    return false;
  }

  private boolean eval( final RenderNode node ) {
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
