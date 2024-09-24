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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.util.beans.LocaleValueConverter;
import org.pentaho.reporting.engine.classic.core.util.beans.TimeZoneValueConverter;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * A simple implementation that provides static environmental information. The environment properties are mapped against
 * the global report configuration.
 *
 * @author Thomas Morgner
 */
public class DefaultReportEnvironment implements ReportEnvironment {
  public static final String ENVIRONMENT_KEY = "org.pentaho.reporting.engine.classic.core.environment.";
  public static final String ENVIRONMENT_TYPE = "org.pentaho.reporting.engine.classic.core.environment-type.";
  private static final Log logger = LogFactory.getLog( DefaultReportEnvironment.class );

  private Configuration configuration;
  private Locale locale;
  private Locale localeFromConfiguration;
  private TimeZone timeZone;
  private TimeZone timeZoneFromConfiguration;

  public DefaultReportEnvironment( final Configuration configuration ) {
    if ( configuration == null ) {
      throw new NullPointerException();
    }
    update( configuration );
  }

  public Object getEnvironmentProperty( final String key ) {
    if ( "engine.version".equals( key ) ) {
      return ClassicEngineInfo.getInstance().getVersion();
    } else if ( "engine.version.major".equals( key ) ) {
      return ClassicEngineInfo.getInstance().getReleaseMajor();
    } else if ( "engine.version.minor".equals( key ) ) {
      return ClassicEngineInfo.getInstance().getReleaseMinor();
    } else if ( "engine.version.patch".equals( key ) ) {
      return ClassicEngineInfo.getInstance().getReleaseMilestone();
    } else if ( "engine.version.candidate-token".equals( key ) ) {
      return ClassicEngineInfo.getInstance().getReleaseCandidateToken();
    } else if ( "engine.version.buildnumber".equals( key ) ) {
      return ClassicEngineInfo.getInstance().getReleaseBuildNumber();
    } else if ( "engine.product-id".equals( key ) ) {
      return ClassicEngineInfo.getInstance().getProductId();
    } else if ( "engine.name".equals( key ) ) {
      return ClassicEngineInfo.getInstance().getName();
    } else if ( "locale".equals( key ) ) {
      return getLocale().toString();
    } else if ( "locale-short".equals( key ) ) {
      Locale l = getLocale();
      if ( StringUtils.isEmpty( l.getCountry() ) ) {
        return l.getLanguage();
      }
      return l.getLanguage() + "_" + l.getCountry();
    } else if ( "locale-language".equals( key ) ) {
      return getLocale().getLanguage();
    }

    final String configProperty = configuration.getConfigProperty( ENVIRONMENT_KEY + key );
    final String configType = configuration.getConfigProperty( ENVIRONMENT_TYPE + key );
    if ( configType == null ) {
      return configProperty;
    }
    try {
      final ClassLoader loader = ObjectUtilities.getClassLoader( DefaultReportEnvironment.class );
      final Class aClass = Class.forName( configType, false, loader );
      return ConverterRegistry.toPropertyValue( configProperty, aClass );
    } catch ( Throwable t ) {
      // ignore ..
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Failed to convert typed report-environment property" );
      }
    }
    return configProperty;
  }

  /**
   * Returns the text encoding that should be used to encode URLs.
   *
   * @return the encoding for URLs.
   */
  public String getURLEncoding() {
    return configuration.getConfigProperty( "org.pentaho.reporting.engine.classic.core.URLEncoding" );
  }

  public void update( final Configuration configuration ) {
    if ( configuration == null ) {
      throw new NullPointerException();
    }
    this.configuration = configuration;
    final Object localeFromConfig = getEnvironmentProperty( "designtime.Locale" );
    if ( localeFromConfig == null ) {
      this.localeFromConfiguration = Locale.getDefault();
    } else {
      try {
        this.localeFromConfiguration =
            (Locale) new LocaleValueConverter().toPropertyValue( String.valueOf( localeFromConfig ) );
      } catch ( BeanException e ) {
        this.localeFromConfiguration = Locale.getDefault();
      }
    }

    final Object timeZoneFromConfig = getEnvironmentProperty( "designtime.TimeZone" );
    if ( timeZoneFromConfig == null ) {
      this.timeZoneFromConfiguration = TimeZone.getDefault();
    } else {
      try {
        this.timeZoneFromConfiguration =
            (TimeZone) new TimeZoneValueConverter().toPropertyValue( String.valueOf( timeZoneFromConfig ) );
      } catch ( BeanException e ) {
        this.timeZoneFromConfiguration = TimeZone.getDefault();
      }
    }
  }

  public void setLocale( final Locale locale ) {
    if ( locale == null ) {
      throw new NullPointerException();
    }
    this.locale = locale;
  }

  public void setTimeZone( final TimeZone timeZone ) {
    if ( timeZone == null ) {
      throw new NullPointerException();
    }
    this.timeZone = timeZone;
  }

  public Locale getLocale() {
    if ( locale != null ) {
      return locale;
    }
    return localeFromConfiguration;
  }

  public TimeZone getTimeZone() {
    if ( timeZone != null ) {
      return timeZone;
    }
    return timeZoneFromConfiguration;
  }

  public Object clone() {
    try {
      final DefaultReportEnvironment environment = (DefaultReportEnvironment) super.clone();
      environment.configuration = (Configuration) configuration.clone();
      return environment;
    } catch ( CloneNotSupportedException cne ) {
      throw new IllegalStateException( cne );
    }
  }

  public Map<String, String[]> getUrlExtraParameter() {
    return Collections.emptyMap();
  }
}
