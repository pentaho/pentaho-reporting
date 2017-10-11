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
