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
