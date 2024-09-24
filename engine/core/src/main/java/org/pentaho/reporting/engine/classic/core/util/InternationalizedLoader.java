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
