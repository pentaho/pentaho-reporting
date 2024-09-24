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

package org.pentaho.reporting.libraries.css.resolver.values;

import org.pentaho.reporting.libraries.css.model.StyleKey;

import java.util.HashSet;

/**
 * Creation-Date: 14.12.2005, 12:06:22
 *
 * @author Thomas Morgner
 */
public final class ResolveHandlerModule {
  private StyleKey key;
  // direct dependencies, indirect ones are handled by the
  // dependent classes ...
  private ResolveHandler autoValueHandler;
  private ResolveHandler computedValueHandler;
  private ResolveHandler percentagesValueHandler;
  private StyleKey[] dependentKeys;
  private int weight;

  public ResolveHandlerModule( final StyleKey key,
                               final ResolveHandler autoValueHandler,
                               final ResolveHandler computedValueHandler,
                               final ResolveHandler percentagesValueHandler ) {
    this.key = key;
    this.autoValueHandler = autoValueHandler;
    this.computedValueHandler = computedValueHandler;
    this.percentagesValueHandler = percentagesValueHandler;

    final HashSet dependentKeys = new HashSet();
    if ( autoValueHandler != null ) {
      final StyleKey[] keys = autoValueHandler.getRequiredStyles();
      for ( int i = 0; i < keys.length; i++ ) {
        final StyleKey styleKey = keys[ i ];
        dependentKeys.add( styleKey );
      }
    }

    if ( computedValueHandler != null ) {
      final StyleKey[] keys = computedValueHandler.getRequiredStyles();
      for ( int i = 0; i < keys.length; i++ ) {
        final StyleKey styleKey = keys[ i ];
        dependentKeys.add( styleKey );
      }
    }

    if ( percentagesValueHandler != null ) {
      final StyleKey[] keys = percentagesValueHandler.getRequiredStyles();
      for ( int i = 0; i < keys.length; i++ ) {
        final StyleKey styleKey = keys[ i ];
        dependentKeys.add( styleKey );
      }
    }

    this.dependentKeys = (StyleKey[])
      dependentKeys.toArray( new StyleKey[ dependentKeys.size() ] );
  }

  public StyleKey getKey() {
    return key;
  }

  public ResolveHandler getAutoValueHandler() {
    return autoValueHandler;
  }

  public ResolveHandler getComputedValueHandler() {
    return computedValueHandler;
  }

  public ResolveHandler getPercentagesValueHandler() {
    return percentagesValueHandler;
  }

  public StyleKey[] getDependentKeys() {
    return dependentKeys;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight( final int weight ) {
    this.weight = weight;
  }
}
