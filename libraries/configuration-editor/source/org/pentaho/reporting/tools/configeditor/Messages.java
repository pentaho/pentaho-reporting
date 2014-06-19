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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.tools.configeditor;

import java.util.HashMap;
import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

public class Messages extends ResourceBundleSupport
{
  private static HashMap<Locale,Messages> locales;

  public static Messages getInstance()
  {
    return getInstance(Locale.getDefault());
  }

  public static synchronized Messages getInstance(final Locale locale)
  {
    if (locales == null)
    {
      locales = new HashMap<Locale,Messages>();
      final Messages retval = new Messages(locale, ConfigEditorBoot.BUNDLE_NAME);
      locales.put(locale, retval);
      return retval;
    }

    final Messages o = locales.get(locale);
    if (o != null)
    {
      return o;
    }

    final Messages retval = new Messages(locale, ConfigEditorBoot.BUNDLE_NAME);
    locales.put(locale, retval);
    return retval;
  }

  private Messages(final Locale locale, final String s)
  {
    super(locale, s, ObjectUtilities.getClassLoader(Messages.class));
  }
}
