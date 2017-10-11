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

package org.pentaho.reporting.engine.classic.core.util;

import org.pentaho.reporting.engine.classic.core.ExtendedResourceBundleFactory;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class LibLoaderResourceBundleFactory implements ExtendedResourceBundleFactory {
  private static class LibLoaderResourceBundle extends PropertyResourceBundle {
    private LibLoaderResourceBundle( final InputStream stream ) throws IOException {
      super( stream );
      setParent( parent );
    }

    /**
     * Sets the parent bundle of this bundle. The parent bundle is searched by {@link #getObject getObject} when this
     * bundle does not contain a particular resource.
     *
     * @param parent
     *          this bundle's parent bundle.
     */
    public void setParent( final ResourceBundle parent ) {
      super.setParent( parent );
    }
  }

  private transient HashSet<String> failedLoads;
  private transient LFUMap<String, LibLoaderResourceBundle> successfulLoads;
  private transient ResourceManager manager;
  private transient ResourceKey baseKey;
  private Locale locale;
  private TimeZone timeZone;

  public LibLoaderResourceBundleFactory() {
    this.failedLoads = new HashSet<String>();
    this.successfulLoads = new LFUMap<String, LibLoaderResourceBundle>( 30 );
  }

  public LibLoaderResourceBundleFactory( final ResourceManager manager, final ResourceKey baseKey, final Locale locale,
      final TimeZone timeZone ) {
    this();
    this.manager = manager;
    this.baseKey = baseKey;
    this.locale = locale;
    this.timeZone = timeZone;
  }

  /**
   * Creates a resource bundle for the given key. How that key is interpreted depends on the used concrete
   * implementation of this interface.
   *
   * @param key
   *          the key that identifies the resource bundle
   * @return the created resource bundle
   * @throws MissingResourceException
   *           if no resource bundle for the specified base name can be found
   * @noinspection MagicCharacter
   */
  public ResourceBundle getResourceBundle( final String key ) {
    final String keyAsPath = key.replace( '.', '/' );
    final Locale locale = getLocale();
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

    LibLoaderResourceBundle fullProperties = null;
    if ( fullName != null ) {
      final String propsName = keyAsPath + '_' + fullName + ".properties"; // NON-NLS
      fullProperties = loadProperties( propsName );
    }

    LibLoaderResourceBundle cntryProperties = null;
    if ( cntryName != null ) {
      final String propsName = keyAsPath + '_' + cntryName + ".properties"; // NON-NLS
      cntryProperties = loadProperties( propsName );
    }

    LibLoaderResourceBundle langProperties = null;
    if ( langName != null ) {
      final String propsName = keyAsPath + '_' + langName + ".properties"; // NON-NLS
      langProperties = loadProperties( propsName );
    }

    final String propsName = keyAsPath + ".properties"; // NON-NLS
    final LibLoaderResourceBundle defaultProperties = loadProperties( propsName );

    if ( langProperties == null && cntryProperties == null && fullProperties == null && defaultProperties == null ) {
      throw new MissingResourceException( "No such bundle: " + keyAsPath, key, null );
    }

    if ( fullProperties != null ) {
      if ( cntryProperties != null ) {
        fullProperties.setParent( cntryProperties );
      } else if ( langProperties != null ) {
        fullProperties.setParent( langProperties );
      } else if ( defaultProperties != null ) {
        fullProperties.setParent( defaultProperties );
      }
    }

    if ( cntryProperties != null ) {
      if ( langProperties != null ) {
        cntryProperties.setParent( langProperties );
      } else if ( defaultProperties != null ) {
        cntryProperties.setParent( defaultProperties );
      }
    }

    if ( langProperties != null ) {
      if ( defaultProperties != null ) {
        langProperties.setParent( defaultProperties );
      }
    }

    if ( fullProperties != null ) {
      return fullProperties;
    }
    if ( cntryProperties != null ) {
      return cntryProperties;
    }
    if ( langProperties != null ) {
      return langProperties;
    }
    return defaultProperties;
  }

  private LibLoaderResourceBundle loadProperties( final String propsName ) {
    if ( failedLoads.contains( propsName ) ) {
      return null;
    }

    final LibLoaderResourceBundle bundle = successfulLoads.get( propsName );
    if ( bundle != null ) {
      return bundle;
    }

    if ( manager == null ) {
      manager = new ResourceManager();
    }

    if ( baseKey != null ) {
      try {
        final ResourceKey resourceKey = manager.deriveKey( baseKey, propsName );
        final ResourceData resource = manager.load( resourceKey );

        final InputStream instream = resource.getResourceAsStream( manager );
        try {
          final LibLoaderResourceBundle resourceBundle = new LibLoaderResourceBundle( instream );
          successfulLoads.put( propsName, resourceBundle );
          return resourceBundle;
        } finally {
          instream.close();
        }
      } catch ( Exception e ) {
        // silently ignore ..
      }
    }

    try {
      // try to load the bundle via classpath ...
      final ResourceKey resourceKey = manager.createKey( "res://" + propsName ); // NON-NLS
      final ResourceData resource = manager.load( resourceKey );

      final InputStream instream = resource.getResourceAsStream( manager );
      try {
        final LibLoaderResourceBundle resourceBundle = new LibLoaderResourceBundle( instream );
        successfulLoads.put( propsName, resourceBundle );
        return resourceBundle;
      } finally {
        instream.close();
      }
    } catch ( Exception e ) {
      // ignored ..
      failedLoads.add( propsName );
    }
    return null;
  }

  /**
   * Returns the locale that will be used to create the resource bundle. This locale is also used to initialize the
   * java.text.Format instances used by the report.
   *
   * @return the locale.
   */
  public Locale getLocale() {
    return locale;
  }

  public TimeZone getTimeZone() {
    return timeZone;
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

  public void setResourceLoader( final ResourceManager resourceManager, final ResourceKey contextKey ) {
    this.manager = resourceManager;
    this.baseKey = contextKey;
    this.failedLoads.clear();
    this.successfulLoads.clear();
  }

  public Object clone() throws CloneNotSupportedException {
    final LibLoaderResourceBundleFactory o = (LibLoaderResourceBundleFactory) super.clone();
    o.failedLoads = (HashSet<String>) failedLoads.clone();
    o.successfulLoads = (LFUMap<String, LibLoaderResourceBundle>) successfulLoads.clone();
    return o;
  }

  private void readObject( final ObjectInputStream stream ) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    failedLoads = new HashSet<String>();
    successfulLoads = new LFUMap<String, LibLoaderResourceBundle>( 30 );
  }
}
