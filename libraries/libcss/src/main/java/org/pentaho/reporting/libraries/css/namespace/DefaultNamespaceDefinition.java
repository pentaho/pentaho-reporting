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


package org.pentaho.reporting.libraries.css.namespace;

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

  public DefaultNamespaceDefinition( final String uri,
                                     final ResourceKey defaultStyleSheet,
                                     final String classAttribute,
                                     final String styleAttribute,
                                     final String preferredPrefix ) {
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
   * @param attr the whitespace separated list of tokens
   * @return the contents as string array
   */
  private String[] buildArray( final String attr ) {
    if ( attr == null ) {
      return new String[ 0 ];
    }

    final StringTokenizer strtok = new StringTokenizer( attr );
    final int size = strtok.countTokens();
    final String[] retval = new String[ size ];
    for ( int i = 0; i < retval.length; i++ ) {
      retval[ i ] = strtok.nextToken();
    }
    return retval;
  }

  public String getURI() {
    return uri;
  }

  public String[] getClassAttribute( final String element ) {
    return (String[]) classAttribute.clone();
  }

  public String[] getStyleAttribute( final String element ) {
    return (String[]) styleAttribute.clone();
  }

  public ResourceKey getDefaultStyleSheetLocation() {
    return defaultStyleSheet;
  }

  public String getPreferredPrefix() {
    return preferredPrefix;
  }
}
