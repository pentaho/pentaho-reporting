/*
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
* Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
* All rights reserved.
*/

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
