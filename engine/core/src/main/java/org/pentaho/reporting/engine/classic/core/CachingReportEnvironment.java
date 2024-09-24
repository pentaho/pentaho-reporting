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
