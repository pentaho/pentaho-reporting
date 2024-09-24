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

package org.pentaho.reporting.designer.core.util;

import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Locale;

public class UtilMessages extends Messages {
  private static UtilMessages utilMessages;

  public static Messages getInstance() {
    if ( utilMessages == null ) {
      utilMessages = new UtilMessages();
    }
    return utilMessages;
  }

  /**
   * Creates a new Messages-collection. The locale and baseName will be used to create the resource-bundle that backs up
   * this implementation.
   */
  private UtilMessages() {
    super( Locale.getDefault(), "org.pentaho.reporting.designer.core.util.messages",  // NON-NLS
      ObjectUtilities.getClassLoader( UtilMessages.class ) );
  }
}
