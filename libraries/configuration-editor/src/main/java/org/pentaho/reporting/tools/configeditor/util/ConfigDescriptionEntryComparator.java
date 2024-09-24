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

package org.pentaho.reporting.tools.configeditor.util;

import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;

import java.util.Comparator;

public class ConfigDescriptionEntryComparator implements Comparator<ConfigDescriptionEntry> {
  public ConfigDescriptionEntryComparator() {
  }

  public int compare( final ConfigDescriptionEntry e1, final ConfigDescriptionEntry e2 ) {
    if ( e1 == null ) {
      return 1;
    }
    if ( e2 == null ) {
      return -1;
    }
    if ( e1 == e2 ) {
      return 0;
    }
    return e1.getKeyName().compareTo( e2.getKeyName() );
  }
}
