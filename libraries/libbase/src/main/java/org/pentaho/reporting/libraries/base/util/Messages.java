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

package org.pentaho.reporting.libraries.base.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A helper class for a simplified resource-bundle access. This class simply ignores all resource-bundle related errors
 * and prints place-holder strings if a localization key cannot be found.
 *
 * @author David Kincade
 */
public class Messages extends ResourceBundleSupport {
  /**
   * Creates a new Messages-collection. The locale and baseName will be used to create the resource-bundle that backs up
   * this implementation.
   *
   * @param locale   the locale.
   * @param baseName the baseName of the resource-bundle.
   * @see ResourceBundle#getBundle(String, Locale)
   */
  public Messages( final Locale locale, final String baseName, final ClassLoader classLoader ) {
    super( locale, baseName, classLoader );
  }

  /**
   * Creates a new Messages-collection. The locale and baseName will be used to create the resource-bundle that backs up
   * this implementation.
   *
   * @param locale   the locale.
   * @param baseName the baseName of the resource-bundle.
   * @see ResourceBundle#getBundle(String, Locale)
   * @deprecated Always provide the classloader to be safe when deployed in weird setups. This method will be removed
   * after the next release.
   */
  public Messages( final Locale locale, final String baseName ) {
    this( locale, baseName, ObjectUtilities.getClassLoader( Messages.class ) );
  }

  /**
   * Creates a new Messages-collection. The locale and baseName will be used to create the resource-bundle that backs up
   * this implementation.
   *
   * @param locale         the locale.
   * @param baseName       the baseName of the resource-bundle.
   * @param resourceBundle a predefined resource-bundle.
   * @deprecated Always provide the classloader to be safe when deployed in weird setups. This method will be removed
   * after the next release.
   */
  public Messages( final Locale locale, final ResourceBundle resourceBundle, final String baseName ) {
    super( locale, resourceBundle, baseName, ObjectUtilities.getClassLoader( Messages.class ) );
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key    the resourcebundle key
   * @param param1 the parameter for the message
   * @return the formated string
   */
  public String getString( final String key, final String... param1 ) {
    try {
      return formatMessage( key, param1 );
    } catch ( final MissingResourceException e ) {
      return '!' + key + '!';
    }
  }

  /**
   * Get a formatted error message. The message consists of two parts. The first part is the error numeric Id associated
   * with the key used to identify the message in the resource file. For instance, suppose the error key is
   * MyClass.ERROR_0068_TEST_ERROR. The first part of the error msg would be "0068". The second part of the returned
   * string is simply the <code>msg</code> parameter.
   * <p/>
   * Currently the format is: error key - error msg For instance: "0068 - A test error message."
   * <p/>
   * Currently the format is: error key - error msg For instance: "0069 - You were punched by the donkey."
   *
   * @param key String containing the key that was used to obtain the <code>msg</code> parameter from the resource
   *            file.
   * @param msg String containing the message that was obtained from the resource file using the <code>key</code>
   *            parameter.
   * @return String containing the formatted error message.
   */
  public String formatErrorMessage( final String key, final String msg ) {
    try {
      final int end;
      final int errorMarker = key.indexOf( ".ERROR_" );
      if ( errorMarker < 0 ) {
        end = key.length();
      } else {
        end = Math.min( errorMarker + ".ERROR_0000".length(), key.length() ); //$NON-NLS-1$
      }
      return getString( "MESSUTIL.ERROR_FORMAT_MASK", key.substring( 0, end ), msg ); //$NON-NLS-1$
    } catch ( final Exception e ) {
      return "!MESSUTIL.ERROR_FORMAT_MASK:" + key + '!';
    }
  }

  /**
   * Get a parametrized formatted error message from the resource-bundle. The message consists of two parts. The first
   * part is the error numeric Id associated with the key used to identify the message in the resource file. For
   * instance, suppose the error key is MyClass.ERROR_0069_DONKEY_PUNCH. The first part of the error msg would be
   * "0069". The second part of the returned string is simply the <code>msg</code> parameter.
   * <p/>
   * Currently the format is: error key - error msg For instance: "0069 - You were punched by the donkey."
   *
   * @param key    String containing the key that was used to obtain the <code>msg</code> parameter from the resource
   *               file.
   * @param param1 the parameter for the message
   * @return String containing the formatted error message.
   */
  public String getErrorString( final String key, final String... param1 ) {
    return formatErrorMessage( key, getString( key, param1 ) );
  }
}
