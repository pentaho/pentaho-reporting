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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.counter.numeric;

import org.pentaho.reporting.libraries.css.counter.CounterStyle;

import java.util.HashSet;

public abstract class NumericCounterStyle implements CounterStyle {
  private static final class ReplacementDefinition {
    private char original;
    private char replacement;

    private ReplacementDefinition( final char original, final char replacement ) {
      this.original = original;
      this.replacement = replacement;
    }

    public char getOriginal() {
      return original;
    }

    public char getReplacement() {
      return replacement;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final ReplacementDefinition that = (ReplacementDefinition) o;

      if ( original != that.original ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      return (int) original;
    }
  }

  private HashSet replacements;
  private int base;
  private transient ReplacementDefinition[] cachedDefinitions;
  private String suffix;

  protected NumericCounterStyle( final int base, final String suffix ) {
    this.base = base;
    this.suffix = suffix;
    this.replacements = new HashSet();
  }

  public final void setReplacementChar( final char org, final char other ) {
    this.replacements.add( new ReplacementDefinition( org, other ) );
    this.cachedDefinitions = null;
  }

  public final String getCounterValue( final int index ) {
    if ( cachedDefinitions == null ) {
      cachedDefinitions = (ReplacementDefinition[])
        replacements.toArray( new ReplacementDefinition[ replacements.size() ] );
    }

    String numeric = Integer.toString( index, base );

    for ( int i = 0; i < cachedDefinitions.length; i++ ) {
      final ReplacementDefinition def = cachedDefinitions[ i ];
      numeric = numeric.replace( def.getOriginal(), def.getReplacement() );
    }
    return numeric;
  }

  public String getSuffix() {
    return suffix;
  }
}
