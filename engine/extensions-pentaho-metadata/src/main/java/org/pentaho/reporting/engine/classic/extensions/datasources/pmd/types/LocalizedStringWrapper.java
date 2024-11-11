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


package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types;

import org.pentaho.metadata.model.concept.types.LocalizedString;

import java.util.Map;
import java.util.Set;

public class LocalizedStringWrapper extends LocalizedString {
  private LocalizedString backend;

  public LocalizedStringWrapper( final LocalizedString backend ) {
    this.backend = backend;
  }

  public int hashCode() {
    return backend.getLocaleStringMap().hashCode();
  }

  @Override
  public boolean equals( final Object object ) {
    if ( object == null ) {
      return false;
    }
    if ( object instanceof LocalizedString == false ) {
      return false;
    }
    return backend.equals( object );
  }

  public Set<String> getLocales() {
    return backend.getLocales();
  }

  public Map<String, String> getLocaleStringMap() {
    return backend.getLocaleStringMap();
  }

  public String getLocalizedString( final String locale ) {
    return backend.getLocalizedString( locale );
  }

  public String getString( final String locale ) {
    return backend.getString( locale );
  }

  public void setString( final String locale, final String string ) {
    backend.setString( locale, string );
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "LocalizedStringWrapper" );
    sb.append( "{localeStringMap=" ).append( backend.getLocaleStringMap() );
    sb.append( '}' );
    return sb.toString();
  }
}
