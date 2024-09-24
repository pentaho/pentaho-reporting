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

package org.pentaho.reporting.engine.classic.core.imagemap;

import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageMap implements Serializable {
  private AttributeMap<String> attributes;
  private ArrayList<ImageMapEntry> mapEntries;

  public ImageMap() {
    attributes = new AttributeMap<String>();
    mapEntries = new ArrayList<ImageMapEntry>();
  }

  public void addMapEntry( final ImageMapEntry mapEntry ) {
    if ( mapEntry == null ) {
      throw new NullPointerException();
    }
    mapEntries.add( mapEntry );
  }

  public ImageMapEntry[] getMapEntries() {
    return mapEntries.toArray( new ImageMapEntry[this.mapEntries.size()] );
  }

  public int getMapEntryCount() {
    return mapEntries.size();
  }

  public ImageMapEntry getMapEntry( final int index ) {
    return mapEntries.get( index );
  }

  public void setAttribute( final String namespace, final String attribute, final String value ) {
    attributes.setAttribute( namespace, attribute, value );
  }

  public String getAttribute( final String namespace, final String attribute ) {
    return attributes.getAttribute( namespace, attribute );
  }

  public String[] getNames( final String namespace ) {
    return attributes.getNames( namespace );
  }

  public String[] getNameSpaces() {
    return attributes.getNameSpaces();
  }

  public ImageMapEntry[] getEntriesForPoint( float x, float y ) {
    final ArrayList<ImageMapEntry> list = new ArrayList<ImageMapEntry>();
    for ( int i = 0; i < mapEntries.size(); i++ ) {
      final ImageMapEntry entry = mapEntries.get( i );
      if ( entry.contains( x, y ) ) {
        list.add( entry );
      }
    }
    return list.toArray( new ImageMapEntry[list.size()] );
  }
}
