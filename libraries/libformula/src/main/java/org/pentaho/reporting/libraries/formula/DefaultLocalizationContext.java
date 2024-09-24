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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.typing.Type;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class DefaultLocalizationContext implements LocalizationContext, Serializable {
  private static final String CONFIG_TIMEZONE_KEY = "org.pentaho.reporting.libraries.formula.timezone";

  private static final String CONFIG_LOCALE_KEY = "org.pentaho.reporting.libraries.formula.locale";

  private ArrayList<DateFormat> dateFormats;
  private ArrayList<DateFormat> datetimeFormats;
  private ArrayList<DateFormat> timeFormats;
  private ArrayList<NumberFormat> numberFormats;

  private Locale locale;

  private TimeZone timeZone;

  public DefaultLocalizationContext() {
    dateFormats = new ArrayList<DateFormat>();
    datetimeFormats = new ArrayList<DateFormat>();
    timeFormats = new ArrayList<DateFormat>();
    numberFormats = new ArrayList<NumberFormat>();
  }

  public Locale getLocale() {
    return locale;
  }

  public ResourceBundle getBundle( final String id ) {
    return ResourceBundle.getBundle( id, getLocale() );
  }

  public TimeZone getTimeZone() {
    return timeZone;
  }

  public List<DateFormat> getDateFormats( final Type type ) {
    List<DateFormat> deepClone = new ArrayList<>();
    if ( type.isFlagSet( Type.DATE_TYPE ) ) {
      dateFormats.forEach( dateFormat -> deepClone.add( (DateFormat) dateFormat.clone() ) );
    } else if ( type.isFlagSet( Type.DATETIME_TYPE ) ) {
      datetimeFormats.forEach( dateFormat -> deepClone.add( (DateFormat) dateFormat.clone() ) );
    } else if ( type.isFlagSet( Type.TIME_TYPE ) ) {
      timeFormats.forEach( dateFormat -> deepClone.add( (DateFormat) dateFormat.clone() ) );
    }
    return deepClone;
  }

  public List<NumberFormat> getNumberFormats() {
    return (List<NumberFormat>) numberFormats.clone();
  }

  private String[] createLocale( final String locale ) {
    final StringTokenizer strtok = new StringTokenizer( locale, "_" );
    final String[] retval = new String[ 3 ];
    if ( strtok.hasMoreElements() ) {
      retval[ 0 ] = strtok.nextToken();
    } else {
      retval[ 0 ] = "";
    }
    if ( strtok.hasMoreElements() ) {
      retval[ 1 ] = strtok.nextToken();
    } else {
      retval[ 1 ] = "";
    }
    if ( strtok.hasMoreElements() ) {
      retval[ 2 ] = strtok.nextToken();
    } else {
      retval[ 2 ] = "";
    }
    return retval;
  }

  public void initialize( final Configuration config ) {
    initialize( config, null, null );
  }

  public void initialize( final Configuration config, final Locale locale, final TimeZone timeZone ) {
    if ( config == null ) {
      throw new NullPointerException();
    }

    if ( locale == null ) {
      // setting locale
      final String declaredLocale = config.getConfigProperty( CONFIG_LOCALE_KEY, Locale.getDefault().toString() );
      final String[] declaredLocaleParts = createLocale( declaredLocale );
      this.locale = new Locale( declaredLocaleParts[ 0 ], declaredLocaleParts[ 1 ], declaredLocaleParts[ 2 ] );
    } else {
      this.locale = locale;
    }

    //setting timezone
    if ( timeZone == null ) {
      final String timeZoneId = config.getConfigProperty( CONFIG_TIMEZONE_KEY, TimeZone.getDefault().getID() );
      this.timeZone = TimeZone.getTimeZone( timeZoneId );
    } else {
      this.timeZone = TimeZone.getDefault();
    }

    final Locale[] locales = new Locale[] { getLocale(), Locale.US };
    for ( int i = 0; i < locales.length; i++ ) {
      final Locale activeLocale = locales[ i ];

      datetimeFormats.add( DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL, activeLocale ) );
      dateFormats.add( DateFormat.getDateInstance( DateFormat.FULL, activeLocale ) );

      datetimeFormats.add( DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG, activeLocale ) );
      dateFormats.add( DateFormat.getDateInstance( DateFormat.LONG, activeLocale ) );

      datetimeFormats.add( DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.MEDIUM, activeLocale ) );
      dateFormats.add( DateFormat.getDateInstance( DateFormat.MEDIUM, activeLocale ) );
      timeFormats.add( DateFormat.getTimeInstance( DateFormat.MEDIUM, activeLocale ) );

      datetimeFormats.add( DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, activeLocale ) );
      dateFormats.add( DateFormat.getDateInstance( DateFormat.SHORT, activeLocale ) );
      timeFormats.add( DateFormat.getTimeInstance( DateFormat.SHORT, activeLocale ) );

      numberFormats
        .add( new DecimalFormat( "#0.#############################", new DecimalFormatSymbols( activeLocale ) ) );
      numberFormats.add( NumberFormat.getCurrencyInstance( activeLocale ) );
      numberFormats.add( NumberFormat.getInstance( activeLocale ) );
      numberFormats.add( NumberFormat.getIntegerInstance( activeLocale ) );
      numberFormats.add( NumberFormat.getNumberInstance( activeLocale ) );
      numberFormats.add( NumberFormat.getPercentInstance( activeLocale ) );
    }

    // adding default ISO8 dateformats
    datetimeFormats.add( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.US ) );
    datetimeFormats.add( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US ) );

    dateFormats.add( new SimpleDateFormat( "yyyy-MM-dd", Locale.US ) );
    timeFormats.add( new SimpleDateFormat( "HH:mm:ss", Locale.US ) );
    timeFormats.add( new SimpleDateFormat( "HH:mm", Locale.US ) );

    for ( int i = 0; i < dateFormats.size(); i++ ) {
      final DateFormat dateFormat = dateFormats.get( i );
      dateFormat.setLenient( false );
    }

    for ( int i = 0; i < datetimeFormats.size(); i++ ) {
      final DateFormat dateFormat = datetimeFormats.get( i );
      dateFormat.setLenient( false );
    }

    for ( int i = 0; i < timeFormats.size(); i++ ) {
      final DateFormat dateFormat = timeFormats.get( i );
      dateFormat.setLenient( false );
    }

    for ( int i = 0; i < numberFormats.size(); i++ ) {
      final NumberFormat format = numberFormats.get( i );
      if ( format instanceof DecimalFormat ) {
        final DecimalFormat fmt = (DecimalFormat) format;
        fmt.setParseBigDecimal( true );
      }
    }
  }
}
