/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.libraries.docbundle.metadata;

import org.pentaho.reporting.libraries.base.util.LinkedMap;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

import java.io.Serializable;

public class DefaultBundleManifest implements BundleManifest, Serializable, Cloneable {
  private LinkedMap entries;
  private AttributeMap<String> entryAttributes;
  private static final long serialVersionUID = 5116035029040370976L;

  public DefaultBundleManifest() {
    entries = new LinkedMap( 10, 0.75f );
    entryAttributes = new AttributeMap<String>();
  }

  public void addEntry( final String fullPath, final String mediaType ) {
    if ( fullPath == null ) {
      throw new NullPointerException();
    }


    if ( mediaType == null ) {
      entries.put( fullPath, "" );
    } else {
      entries.put( fullPath, mediaType );
    }
  }

  public void setAttribute( final String entryName, final String attributeName, final String value ) {
    entryAttributes.setAttribute( entryName, attributeName, value );
  }

  public String getAttribute( final String entryName, final String attributeName ) {
    return entryAttributes.getAttribute( entryName, attributeName );
  }

  public String getMimeType( final String entry ) {
    if ( entry == null ) {
      throw new NullPointerException();
    }

    return (String) entries.get( entry );
  }

  public String[] getEntries() {
    return (String[]) entries.keys( new String[ entries.size() ] );
  }

  public boolean removeEntry( final String entry ) {
    if ( entry == null ) {
      throw new NullPointerException();
    }

    final boolean b = entries.remove( entry ) != null;
    final String[] names = entryAttributes.getNames( entry );
    for ( int i = 0; i < names.length; i++ ) {
      final String name = names[ i ];
      entryAttributes.setAttribute( entry, name, null );
    }
    return b;
  }

  public String[] getAttributeNames( final String entryName ) {
    return entryAttributes.getNames( entryName );
  }

  public Object clone() throws CloneNotSupportedException {
    final DefaultBundleManifest manifest = (DefaultBundleManifest) super.clone();
    manifest.entries = (LinkedMap) entries.clone();
    manifest.entryAttributes = (AttributeMap<String>) entryAttributes.clone();
    return manifest;
  }
}


