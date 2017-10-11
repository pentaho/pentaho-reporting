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

package org.pentaho.reporting.engine.classic.core.style.css;

import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

import java.io.Serializable;
import java.util.ArrayList;

public class ElementStyleDefinition implements Serializable, Cloneable {
  private ArrayList<ElementStyleDefinition> styles;
  private ArrayList<ElementStyleSheet> rules;

  public ElementStyleDefinition() {
    rules = new ArrayList<ElementStyleSheet>();
    styles = new ArrayList<ElementStyleDefinition>();
  }

  public ElementStyleSheet getRule( final int index ) {
    return rules.get( index );
  }

  public void addRule( final ElementStyleSheet rule ) {
    this.rules.add( rule );
  }

  public void addRule( final int index, final ElementStyleSheet rule ) {
    this.rules.add( index, rule );
  }

  public void removeRule( final int index ) {
    this.rules.remove( index );
  }

  public void removeRule( final ElementStyleSheet rule ) {
    this.rules.remove( rule );
  }

  /**
   * Iterate over the rules looking for a match based on the style sheet id. If found, we remove the old style sheet and
   * insert the new one in the same location.
   *
   * @param rule
   * @return true if the update happened.
   */
  public boolean updateRule( final ElementStyleSheet rule ) {
    for ( int index = 0; index < getRuleCount(); index++ ) {
      final ElementStyleSheet styleDefinition = getRule( index );
      if ( ( styleDefinition != null ) && ( styleDefinition.getId() == rule.getId() ) ) {
        removeRule( index );
        addRule( index, rule );

        return true;
      }
    }

    return false;
  }

  public void clearRules() {
    this.rules.clear();
  }

  public int getRuleCount() {
    return rules.size();
  }

  public int getStyleSheetCount() {
    return styles.size();
  }

  public ElementStyleDefinition getStyleSheet( final int index ) {
    return styles.get( index );
  }

  public void addStyleSheet( final ElementStyleDefinition rule ) {
    this.styles.add( rule );
  }

  public void addStyleSheet( final int index, final ElementStyleDefinition rule ) {
    this.styles.add( index, rule );
  }

  public void removeStyleSheet( final int index ) {
    this.styles.remove( index );
  }

  public void removeStyleSheet( final ElementStyleDefinition rule ) {
    this.styles.remove( rule );
  }

  public void clearStyleSheet() {
    this.styles.clear();
  }

  public ElementStyleDefinition clone() {
    try {
      final ElementStyleDefinition clone = (ElementStyleDefinition) super.clone();
      clone.styles = (ArrayList<ElementStyleDefinition>) styles.clone();
      for ( int i = 0; i < styles.size(); i++ ) {
        final ElementStyleDefinition styleDefinition = styles.get( i );
        clone.styles.set( i, styleDefinition.clone() );
      }
      clone.rules = (ArrayList<ElementStyleSheet>) rules.clone();
      for ( int i = 0; i < rules.size(); i++ ) {
        final ElementStyleSheet styleDefinition = rules.get( i );
        clone.rules.set( i, styleDefinition.clone() );
      }
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public ElementStyleDefinition derive( final boolean preserveIds ) {
    try {
      final ElementStyleDefinition clone = (ElementStyleDefinition) super.clone();
      clone.styles = (ArrayList<ElementStyleDefinition>) styles.clone();
      for ( int i = 0; i < styles.size(); i++ ) {
        final ElementStyleDefinition styleDefinition = styles.get( i );
        clone.styles.set( i, styleDefinition.derive( preserveIds ) );
      }
      clone.rules = (ArrayList<ElementStyleSheet>) rules.clone();
      for ( int i = 0; i < rules.size(); i++ ) {
        final ElementStyleSheet styleDefinition = rules.get( i );
        clone.rules.set( i, styleDefinition.derive( preserveIds ) );
      }
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

}
