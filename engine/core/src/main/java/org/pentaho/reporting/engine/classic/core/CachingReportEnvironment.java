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


package org.pentaho.reporting.engine.classic.core;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CachingReportEnvironment implements ReportEnvironment {
  private static final Object NULLOBJECT = new Object();

  private ReportEnvironment backend;
  private Locale locale;
  private TimeZone timeZone;
  private String urlEncoding;
  private HashMap<String, Object> properties;

  public CachingReportEnvironment( final ReportEnvironment backend ) {
    this.backend = backend;
    this.properties = new HashMap<String, Object>();
  }

  public Object getEnvironmentProperty( final String key ) {
    final Object fromCache = properties.get( key );
    if ( fromCache != null ) {
      if ( fromCache != NULLOBJECT ) {
        return fromCache;
      }
      return null;
    }

    final Object fromBackend = backend.getEnvironmentProperty( key );
    if ( fromBackend == null ) {
      properties.put( key, NULLOBJECT );
    } else {
      properties.put( key, fromBackend );
    }
    return fromBackend;
  }

  public String getURLEncoding() {
    if ( urlEncoding == null ) {
      urlEncoding = backend.getURLEncoding();
    }
    return urlEncoding;
  }

  public Locale getLocale() {
    if ( locale == null ) {
      locale = backend.getLocale();
    }
    return locale;
  }

  public TimeZone getTimeZone() {
    if ( timeZone == null ) {
      timeZone = backend.getTimeZone();
    }
    return timeZone;
  }

  public Map<String, String[]> getUrlExtraParameter() {
    return backend.getUrlExtraParameter();
  }

  public Object clone() {
    return this;
  }
}
