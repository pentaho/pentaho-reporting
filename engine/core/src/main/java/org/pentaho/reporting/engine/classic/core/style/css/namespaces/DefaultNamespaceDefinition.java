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

package org.pentaho.reporting.engine.classic.core.style.css.namespaces;

import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import java.util.StringTokenizer;

/**
 * A default implementation of the NamespaceDefinition interface. This implementation assumes that all elements use the
 * same style and class attributes.
 *
 * @author Thomas Morgner
 */
public class DefaultNamespaceDefinition implements NamespaceDefinition {
  private String uri;
  private String[] classAttribute;
  private String[] styleAttribute;
  private ResourceKey defaultStyleSheet;
  private String preferredPrefix;

  public DefaultNamespaceDefinition( final String uri, final ResourceKey defaultStyleSheet,
      final String classAttribute, final String styleAttribute, final String preferredPrefix ) {
    if ( uri == null ) {
      throw new NullPointerException();
    }
    this.uri = uri;
    this.defaultStyleSheet = defaultStyleSheet;
    this.classAttribute = buildArray( classAttribute );
    this.styleAttribute = buildArray( styleAttribute );
    this.preferredPrefix = preferredPrefix;
  }

  /**
   * This method accepts a whitespace separated list of tokens and transforms it into a String array.
   *
   * @param attr
   *          the whitespace separated list of tokens
   * @return the contents as string array
   */
  private String[] buildArray( final String attr ) {
    if ( attr == null ) {
      return new String[0];
    }

    final StringTokenizer strtok = new StringTokenizer( attr );
    final int size = strtok.countTokens();
    final String[] retval = new String[size];
    for ( int i = 0; i < retval.length; i++ ) {
      retval[i] = strtok.nextToken();
    }
    return retval;
  }

  public String getURI() {
    return uri;
  }

  public String[] getClassAttribute( final String element ) {
    return classAttribute.clone();
  }

  public String[] getStyleAttribute( final String element ) {
    return styleAttribute.clone();
  }

  public ResourceKey getDefaultStyleSheetLocation() {
    return defaultStyleSheet;
  }

  public String getPrefix() {
    return preferredPrefix;
  }
}
