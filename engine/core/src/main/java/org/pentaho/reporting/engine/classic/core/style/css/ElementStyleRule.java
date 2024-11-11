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


package org.pentaho.reporting.engine.classic.core.style.css;

import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelector;

import java.util.ArrayList;
import java.util.List;

public class ElementStyleRule extends ElementStyleSheet {
  private ArrayList<CSSSelector> selectorList;

  public ElementStyleRule() {
    selectorList = new ArrayList<CSSSelector>();
  }

  public List<CSSSelector> getSelectorList() {
    return selectorList;
  }

  public void addSelector( final CSSSelector selector ) {
    this.selectorList.add( selector );
  }

  public void addSelector( final int index, final CSSSelector selector ) {
    this.selectorList.add( index, selector );
  }

  public CSSSelector getSelector( final int index ) {
    return this.selectorList.get( index );
  }

  public void removeSelector( final int index ) {
    this.selectorList.remove( index );
  }

  public void removeSelector( final CSSSelector element ) {
    this.selectorList.remove( element );
  }

  public int getSelectorCount() {
    return selectorList.size();
  }

  public ElementStyleRule clone() {
    final ElementStyleRule clone = (ElementStyleRule) super.clone();
    clone.selectorList = (ArrayList<CSSSelector>) selectorList.clone();
    return clone;
  }

  public ElementStyleRule derive( final boolean preserveId ) {
    final ElementStyleRule clone = (ElementStyleRule) super.derive( preserveId );
    clone.selectorList = (ArrayList<CSSSelector>) selectorList.clone();
    return clone;
  }

  public void clearSelectors() {
    selectorList.clear();
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof ElementStyleRule ) ) {
      return false;
    }

    final ElementStyleRule that = (ElementStyleRule) o;

    if ( selectorList != null ? !selectorList.equals( that.selectorList ) : that.selectorList != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return selectorList != null ? selectorList.hashCode() : 0;
  }
}
