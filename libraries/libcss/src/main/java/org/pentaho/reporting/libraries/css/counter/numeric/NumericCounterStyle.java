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
