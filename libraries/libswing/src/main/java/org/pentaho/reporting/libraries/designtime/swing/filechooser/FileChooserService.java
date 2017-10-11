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

package org.pentaho.reporting.libraries.designtime.swing.filechooser;

import java.io.File;
import java.util.prefs.Preferences;

public class FileChooserService {
  private static final String LOCATION_POLICY =
    "org/pentaho/reporting/libraries/designtime/swing/CommonFileChooser/locationpolicy";
  private static final String LOCATION_PREFS =
    "org/pentaho/reporting/libraries/designtime/swing/CommonFileChooser/locations";
  private static final String STATIC_PREFS =
    "org/pentaho/reporting/libraries/designtime/swing/CommonFileChooser/staticprefs";

  public static final String DEFAULT_KEY = "__default__";
  private static FileChooserService instance;
  private Preferences locationPolicy;
  private Preferences staticLocationMappings;
  private Preferences resourceLocationMappings;

  public static synchronized FileChooserService getInstance() {
    if ( instance == null ) {
      instance = new FileChooserService();
    }
    return instance;
  }

  public FileChooserService() {
    locationPolicy = Preferences.userRoot().node( LOCATION_POLICY );
    resourceLocationMappings = Preferences.userRoot().node( LOCATION_PREFS );
    staticLocationMappings = Preferences.userRoot().node( STATIC_PREFS );
  }

  public CommonFileChooser getFileChooser( final String fileType ) {
    return new SwingFileChooserService( fileType );
  }

  public boolean isStoreLocations( final String key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    return locationPolicy.getBoolean( key, locationPolicy.getBoolean( DEFAULT_KEY, true ) );
  }

  public void setStoreLocations( final String key, final boolean storeLocations ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    this.locationPolicy.putBoolean( key, storeLocations );
  }

  public File getStaticLocation( final String aKey ) {
    final String staticLocationValue = staticLocationMappings.get( aKey, null );
    if ( staticLocationValue != null ) {
      return new File( staticLocationValue );
    }
    return null;
  }

  public void setStaticLocation( final String aKey, final File file ) {
    if ( file == null ) {
      staticLocationMappings.remove( aKey );
    } else {
      staticLocationMappings.put( aKey, file.getAbsolutePath() );
    }
  }

  public void setLastLocation( final String aKey, final File file ) {
    if ( file == null ) {
      resourceLocationMappings.remove( aKey );
    } else {
      resourceLocationMappings.put( aKey, file.getAbsolutePath() );
    }
  }

  public File getLastLocation( final String aKey ) {
    final String staticLocationValue = resourceLocationMappings.get( aKey, null );
    if ( staticLocationValue != null ) {
      return new File( staticLocationValue );
    }
    return null;
  }


}
