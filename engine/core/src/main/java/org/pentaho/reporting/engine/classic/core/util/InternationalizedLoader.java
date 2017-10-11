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

package org.pentaho.reporting.engine.classic.core.util;

import java.util.Locale;

public abstract class InternationalizedLoader<T> {

  protected InternationalizedLoader() {
  }

  protected T load( final String keyAsPath, final Locale locale ) {
    final String variant = locale.getVariant();
    final String country = locale.getCountry();
    final String language = locale.getLanguage();

    final String fullName;
    if ( "".equals( variant ) == false ) {
      fullName = locale.getLanguage() + '_' + locale.getCountry() + '_' + locale.getVariant();
    } else {
      fullName = null;
    }

    final String cntryName;
    if ( "".equals( country ) == false ) {
      cntryName = locale.getLanguage() + '_' + locale.getCountry();
    } else {
      cntryName = null;
    }

    final String langName;
    if ( "".equals( language ) == false ) {
      langName = locale.getLanguage();
    } else {
      langName = null;
    }

    if ( fullName != null ) {
      final String propsName = keyAsPath + '_' + fullName + getExtension(); // NON-NLS
      final T value = loadData( propsName );
      if ( value != null ) {
        return value;
      }
    }

    if ( cntryName != null ) {
      final String propsName = keyAsPath + '_' + cntryName + getExtension(); // NON-NLS
      final T value = loadData( propsName );
      if ( value != null ) {
        return value;
      }
    }

    if ( langName != null ) {
      final String propsName = keyAsPath + '_' + langName + getExtension(); // NON-NLS
      final T value = loadData( propsName );
      if ( value != null ) {
        return value;
      }
    }

    final String propsName = keyAsPath + getExtension(); // NON-NLS
    final T value = loadData( propsName );
    if ( value != null ) {
      return value;
    }

    return null;
  }

  protected abstract String getExtension();

  protected abstract T loadData( final String name );

}
