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
