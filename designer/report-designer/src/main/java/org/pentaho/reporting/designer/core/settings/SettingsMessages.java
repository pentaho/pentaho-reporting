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

package org.pentaho.reporting.designer.core.settings;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

import java.util.Locale;

/**
 * Todo: Document me!
 * <p/>
 * Date: 07.12.2009 Time: 15:14:55
 *
 * @author Thomas Morgner.
 */
public class SettingsMessages extends ResourceBundleSupport {
  private static SettingsMessages instance;

  /**
   * Creates a new instance.
   */
  private SettingsMessages() {
    super( Locale.getDefault(), "org.pentaho.reporting.designer.core.settings.messages.messages",//NON-NLS
      ObjectUtilities.getClassLoader( SettingsMessages.class ) );
  }

  public static synchronized SettingsMessages getInstance() {
    if ( instance == null ) {
      instance = new SettingsMessages();
    }
    return instance;
  }
}

