/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
