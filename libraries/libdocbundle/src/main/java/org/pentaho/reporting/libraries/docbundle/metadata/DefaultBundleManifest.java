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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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


