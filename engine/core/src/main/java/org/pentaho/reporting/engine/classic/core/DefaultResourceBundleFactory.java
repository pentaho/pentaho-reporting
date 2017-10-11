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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * A default implementation of the ResourceBundleFactory, that creates resource bundles using the specified locale.
 * <p/>
 * If not defined otherwise, this implementation uses <code>Locale.getDefault()</code> as Locale.
 *
 * @author Thomas Morgner
 */
public class DefaultResourceBundleFactory implements ResourceBundleFactory {
  /**
   * The locale used by this factory.
   */
  private Locale locale;
  /**
   * The timezone used by this factory.
   */
  private TimeZone timeZone;

  /**
   * Creates a new DefaultResourceBundleFactory using the system's default locale as factory locale.
   */
  public DefaultResourceBundleFactory() {
    this( Locale.getDefault() );
  }

  /**
   * Creates a new DefaultResourceBundleFactory using the specified locale as factory locale.
   *
   * @param locale
   *          the Locale instance that should be used when creating ResourceBundles.
   * @throws NullPointerException
   *           if the given Locale is null.
   */
  public DefaultResourceBundleFactory( final Locale locale ) {
    this( locale, TimeZone.getDefault() );
  }

  public DefaultResourceBundleFactory( final Locale locale, final TimeZone timeZone ) {
    if ( locale == null ) {
      throw new NullPointerException( "Locale must not be null" );
    }
    if ( timeZone == null ) {
      throw new NullPointerException( "TimeZone must not be null" );
    }
    this.locale = locale;
    this.timeZone = timeZone;
  }

  public TimeZone getTimeZone() {
    return timeZone;
  }

  /**
   * Returns the locale that will be used to create the resource bundle.
   *
   * @return the locale.
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Redefines the locale. The locale given must not be null.
   *
   * @param locale
   *          the new locale (never null).
   * @throws NullPointerException
   *           if the given locale is null.
   */
  public void setLocale( final Locale locale ) {
    if ( locale == null ) {
      throw new NullPointerException( "Locale must not be null" );
    }
    this.locale = locale;
  }

  /**
   * Creates a resource bundle named by the given key and using the factory's defined locale.
   *
   * @param key
   *          the name of the resourcebundle, never null.
   * @return the created resource bundle
   * @throws NullPointerException
   *           if <code>key</code> is <code>null</code>
   * @throws java.util.MissingResourceException
   *           if no resource bundle for the specified base name can be found
   * @see ResourceBundle#getBundle(String, Locale)
   */
  public ResourceBundle getResourceBundle( final String key ) {
    return ResourceBundle.getBundle( key, locale );
  }
}
