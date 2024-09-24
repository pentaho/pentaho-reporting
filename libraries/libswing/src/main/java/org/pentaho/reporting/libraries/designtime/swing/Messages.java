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
