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

package org.pentaho.reporting.libraries.designtime.swing.settings;

import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import java.awt.*;
import java.util.prefs.Preferences;

public class DialogSizeSettings {
  private Preferences properties;

  public DialogSizeSettings() {
    properties =
      Preferences.userRoot().node( "org/pentaho/reporting/libraries/designtime/swing/dialog-settings" ); // NON-NLS
  }


  public void put( final String key, final Rectangle value ) {
    if ( key == null ) {
      throw new IllegalArgumentException( "key must not be null" );
    }

    if ( value == null ) {
      properties.remove( key );
    } else {
      properties.put( key, LibSwingUtil.rectangleToString( value ) );
    }
  }

  public Rectangle get( final String key ) {
    final String value = properties.get( key, null );
    if ( value == null ) {
      return null;
    }
    return LibSwingUtil.parseRectangle( value );
  }

}
