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


package org.pentaho.reporting.libraries.designtime.swing.settings;

import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.util.Locale;
import java.util.TimeZone;
import java.util.prefs.Preferences;

public class DefaultLocaleSettings implements LocaleSettings {
  private static final String DATE_FORMAT_PATTERN = "DateFormatPattern";
  private static final String TIME_FORMAT_PATTERN = "TimeFormatPattern";
  private static final String DATETIME_FORMAT_PATTERN = "DatetimeFormatPattern";

  private Preferences properties;

  public DefaultLocaleSettings() {
    properties =
      Preferences.userRoot().node( "org/pentaho/reporting/libraries/designtime/swing/locale-settings" ); // NON-NLS
  }


  private void put( final String key, final String value ) {
    if ( key == null ) {
      throw new IllegalArgumentException( "key must not be null" );
    }

    if ( StringUtils.isEmpty( value ) ) {
      properties.remove( key );
    } else {
      properties.put( key, value );
    }
  }

  private String getString( final String key ) {
    return properties.get( key, null );
  }


  public String getDateFormatPattern() {
    final String s = getString( DATE_FORMAT_PATTERN );
    if ( StringUtils.isEmpty( s ) ) {
      return "yyyy-MM-dd";// NON-NLS
    }
    return s;
  }

  public void setDateFormatPattern( final String dateFormatPattern ) {
    put( DATE_FORMAT_PATTERN, dateFormatPattern );
  }

  public String getTimeFormatPattern() {
    final String s = getString( DATE_FORMAT_PATTERN );
    if ( StringUtils.isEmpty( s ) ) {
      return "HH:mm:ss.SSSS"; // NON-NLS
    }
    return s;
  }

  public void setTimeFormatPattern( final String timeFormatPattern ) {
    put( TIME_FORMAT_PATTERN, timeFormatPattern );
  }

  public String getDatetimeFormatPattern() {
    final String s = getString( DATE_FORMAT_PATTERN );
    if ( StringUtils.isEmpty( s ) ) {
      return "yyyy-MM-dd HH:mm:ss.SSSS";// NON-NLS
    }
    return s;
  }

  public void setDatetimeFormatPattern( final String datetimeFormatPattern ) {
    put( DATETIME_FORMAT_PATTERN, datetimeFormatPattern );
  }

  public Locale getLocale() {
    return Locale.getDefault();
  }

  public TimeZone getTimeZone() {
    return TimeZone.getDefault();
  }
}
