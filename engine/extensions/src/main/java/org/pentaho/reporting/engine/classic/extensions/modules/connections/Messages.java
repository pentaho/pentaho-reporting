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
 * Copyright (c) 2005-2017 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;

public class Messages extends ResourceBundleSupport {
  private static Messages instance;

  public static Messages getInstance() {
    // its ok that this one is not synchronized. I don't care whether we have multiple instances of this
    // beast sitting around, as this is a singleton for convenience reasons.
    if ( instance == null ) {
      instance = new Messages();
    }
    return instance;
  }

  /**
   * Creates a new instance.
   */
  private Messages() {
    super( Locale.getDefault(), "org.pentaho.reporting.engine.classic.extensions.modules.connections.messages",
        ObjectUtilities.getClassLoader( Messages.class ) );
  }
}
