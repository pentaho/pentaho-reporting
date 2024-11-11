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


package org.pentaho.reporting.libraries.designtime.swing;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import java.util.HashMap;
import java.util.Locale;

/**
 * A translation bundle.
 *
 * @author Thomas Morgner
 */
public class Messages extends ResourceBundleSupport {
  private static final String BUNDLENAME = "org.pentaho.reporting.libraries.designtime.swing.messages";
  private static HashMap<Locale, Messages> locales;

  /**
   * Returns a instance for the current default locale.
   *
   * @return the messages-bundle for the default locale.
   */
  public static Messages getInstance() {
    return getInstance( Locale.getDefault() );
  }

  /**
   * Returns the messages-bundle for the given locale.
   *
   * @param locale the locale, never null.
   * @return the message bundle for this locale.
   */
  public static synchronized Messages getInstance( final Locale locale ) {
    if ( locale == null ) {
      throw new NullPointerException();
    }

    if ( locales == null ) {
      locales = new HashMap<Locale, Messages>();
      final Messages retval = new Messages( locale, BUNDLENAME );
      locales.put( locale, retval );
      return retval;
    }

    final Messages o = locales.get( locale );
    if ( o != null ) {
      return o;
    }

    final Messages retval = new Messages( locale, BUNDLENAME );
    locales.put( locale, retval );
    return retval;
  }

  /**
   * A private constructor so that no outsider can create a bundle.
   *
   * @param locale the locale for which to create a message bundle.
   * @param s      the locale name,
   */
  private Messages( final Locale locale, final String s ) {
    super( locale, s, ObjectUtilities.getClassLoader( Messages.class ) );
  }
}
