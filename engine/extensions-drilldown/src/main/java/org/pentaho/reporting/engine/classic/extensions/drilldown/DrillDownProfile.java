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

package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.pentaho.reporting.engine.classic.core.metadata.AbstractMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DrillDownProfile extends AbstractMetaData {
  private Class linkCustomizerType;
  private HashMap<String, String> attributes;

  public DrillDownProfile( final String name,
                           final String bundleLocation,
                           final String keyPrefix,
                           final boolean expert,
                           final boolean preferred,
                           final boolean hidden,
                           final boolean deprecated,
                           final Class linkCustomizerType,
                           final Map<String, String> attributes,
                           final MaturityLevel maturityLevel,
                           final int compatibilityLevel ) {
    super( name, bundleLocation, keyPrefix, expert, preferred, hidden, deprecated, maturityLevel, compatibilityLevel );
    this.linkCustomizerType = linkCustomizerType;
    this.attributes = new HashMap<String, String>( attributes );
  }

  public DrillDownProfile( final Class linkCustomizerType ) {
    this( "", "org.pentaho.reporting.engine.classic.extensions.drilldown.drilldown-profile",
      "", false, false, false, false, linkCustomizerType, new HashMap<String, String>(), MaturityLevel.Production, -1 );
  }

  public Class getLinkCustomizerType() {
    return linkCustomizerType;
  }

  public String getAttribute( final String name ) {
    return attributes.get( name );
  }

  public String[] getAttributes() {
    return attributes.keySet().toArray( new String[ attributes.size() ] );
  }

  public String getGroupDisplayName( final Locale locale ) {
    return getBundle( locale )
      .getString( "drilldown-profile-group." + getAttribute( "group" ) + ".display-name" );//NON-NLS
  }
}
